/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.process;

import com.liferay.portal.kernel.concurrent.ConcurrentHashSet;
import com.liferay.portal.kernel.image.ImageMagickUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Variation of <code>ProcessExecutor</code> targeted specifically for
 * executing native processes.
 *
 * @author Ivica Cardic
 */
public class NativeProcessExecutor {
	public static <T extends Serializable> Future<T> execute(
		List<String> arguments)
		throws ProcessException {

		return execute(arguments, null);
	}

	public static <T extends Serializable> Future<T> execute(
		List<String> arguments, NativeProcessCallable<T> processCallable)
		throws ProcessException {

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(
				arguments.toArray(new String[arguments.size()]));

			Map<String, String> env = processBuilder.environment();
			try {
				env.put("LIBPATH", ImageMagickUtil.getGlobalSearchPath());
			} catch (Exception e) {
				e.printStackTrace();
			}

			Process process = processBuilder.start();

			SubprocessReactor subprocessReactor =
				new SubprocessReactor(process, processCallable);

			ExecutorService executorService = _getExecutorService();

			try {
				Future<ProcessCallable<? extends Serializable>>
					futureResponseProcessCallable = executorService.submit(
					subprocessReactor);

				// Consider the newly created process as a managed process only
				// after the subprocess reactor is taken by the thread pool

				_managedProcesses.add(process);

				if (_log.isDebugEnabled()) {
					executorService.submit(
						new ProcessLogRunnable(
							process.getInputStream(),
							ProcessLogRunnable.LogLevel.DEBUG));

					executorService.submit(
						new ProcessLogRunnable(
							process.getInputStream(),
							ProcessLogRunnable.LogLevel.ERROR));
				}

				return new ProcessExecutionFutureResult<T>(
					futureResponseProcessCallable, process);
			} catch (RejectedExecutionException ree) {
				process.destroy();

				throw new ProcessException(
					"Cancelled execution because of a concurrent destroy", ree);
			}
		} catch (IOException e) {
			throw new ProcessException(e);
		}
	}

	public void destroy() {
		if (_executorService == null) {
			return;
		}

		synchronized (ProcessExecutor.class) {
			if (_executorService != null) {
				_executorService.shutdownNow();

				// At this point, the thread pool will no longer take in any
				// more subprocess reactors, so we know the list of managed
				// processes is in a safe state. The worst case is that the
				// destroyer thread and the thread pool thread concurrently
				// destroy the same process, but this is JDK's job to ensure
				// that processes are destroyed in a thread safe manner.

				Iterator<Process> iterator = _managedProcesses.iterator();

				while (iterator.hasNext()) {
					Process process = iterator.next();

					process.destroy();

					iterator.remove();
				}

				// The current thread has a more comprehensive view of the list
				// of managed processes than any thread pool thread. After the
				// previous iteration, we are safe to clear the list of managed
				// processes.

				_managedProcesses.clear();

				_executorService = null;
			}
		}
	}

	private static ExecutorService _getExecutorService() {
		if (_executorService != null) {
			return _executorService;
		}

		synchronized (ProcessExecutor.class) {
			if (_executorService == null) {
				_executorService = Executors.newCachedThreadPool(
					new NamedThreadFactory(
						ProcessExecutor.class.getName(), Thread.MIN_PRIORITY,
						PortalClassLoaderUtil.getClassLoader()));
			}
		}

		return _executorService;
	}

	private static Log _log = LogFactoryUtil.getLog(ProcessExecutor.class);

	private static volatile ExecutorService _executorService;
	private static Set<Process> _managedProcesses =
		new ConcurrentHashSet<Process>();

	private static class ProcessExecutionFutureResult<T> implements Future<T> {

		public ProcessExecutionFutureResult(Future future, Process process) {
			_future = future;
			_process = process;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			if (_future.isCancelled() || _future.isDone()) {
				return false;
			}

			_future.cancel(true);
			_process.destroy();

			return true;
		}

		public boolean isCancelled() {
			return _future.isCancelled();
		}

		public boolean isDone() {
			return _future.isDone();
		}

		public T get() throws ExecutionException, InterruptedException {
			return _future.get();
		}

		public T get(long timeout, TimeUnit timeUnit)
			throws ExecutionException, InterruptedException, TimeoutException {

			return _future.get(timeout, timeUnit);
		}

		private final Future<T> _future;
		private final Process _process;

	}

	private static class ProcessLogRunnable implements Runnable {
		public static enum LogLevel {DEBUG, INFO, WARN, ERROR}

		InputStream is;
		LogLevel logLevel;

		ProcessLogRunnable(InputStream is, LogLevel logLevel) {
			this.is = is;
			this.logLevel = logLevel;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					switch (logLevel) {
						case DEBUG:
							_log.debug(line);
						case INFO:
							_log.info(line);
						case WARN:
							_log.warn(line);
						case ERROR:
							_log.error(line);
					}
				}
			} catch (IOException ioe) {
				_log.debug(ioe);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}

			}
		}
	}

	private static class SubprocessReactor
		<T extends Serializable, P extends NativeProcessCallable<T>>
		implements Callable<T> {

		public SubprocessReactor(Process process, P processCallable) {
			_process = process;
			_processCallable = processCallable;
		}

		public T call() throws Exception {
			try {
				if (_processCallable != null) {
					InputStream is = _process.getInputStream();
					try {
						_processCallable.setInputStream(is);

						return _processCallable.call();
					} finally {
						is.close();
					}
				}
			} finally {
				try {
					int exitCode = _process.waitFor();

					if (exitCode != 0) {
						throw new ProcessException(
							"Subprocess terminated with exit code " + exitCode);
					}
				} catch (InterruptedException ie) {
					_process.destroy();

					throw new ProcessException(
						"Forcibly killed subprocess on interruption", ie);
				}

				_managedProcesses.remove(_process);
			}

			return null;
		}

		private final Process _process;
		private final NativeProcessCallable<T> _processCallable;
	}

}