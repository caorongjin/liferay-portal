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

package com.liferay.portal.image;

import com.liferay.portal.kernel.image.GhostScript;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.process.NativeProcessExecutor;
import com.liferay.portal.kernel.util.OSDetector;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Ivica Cardic
 */
public class GhostScriptImpl implements GhostScript {

	public Future convert(List<String> arguments) throws Exception {

		if (!isEnabled()) {
			throw new IllegalStateException(
				"Cannot call \"" + getCommandPath() +
					"\" when GhostScript is disabled. Enable ImageMagick " +
					"along with GhostScript.");
		}

		LinkedList<String> commands = new LinkedList<String>();

		commands.add(_commandPath);
		commands.add("-dBATCH");
		commands.add("-dSAFER");
		commands.add("-dNOPAUSE");
		commands.add("-dNOPROMPT");
		commands.add("-sFONTPATH" + getGlobalSearchPath());

		commands.addAll(arguments);

		return NativeProcessExecutor.execute(commands);
	}

	public boolean isEnabled() {
		return ImageMagickImpl.getInstance().isEnabled();
	}

	public void reset() {
		if (isEnabled()) {
			try {
				_commandPath = getCommandPath();
			}
			catch (Exception e) {
				_log.warn(e, e);
			}
		}
	}

	private String getCommandPath() throws Exception {
		String commandPath;

		if (OSDetector.isWindows()) {
			commandPath = searchForCommandPath(GHOSTSCRIPT_CMD_WINDOWS_64);
			if (commandPath == null) {
				commandPath = searchForCommandPath(GHOSTSCRIPT_CMD_WINDOWS_32);
			}
		}else {
			commandPath = searchForCommandPath(GHOSTSCRIPT_CMD_UNIX);
		}

		if (commandPath == null) {
			throw new FileNotFoundException(
				"Ghostscript command doesn't exists.");
		}

		return commandPath;
	}

	private String getGlobalSearchPath() throws Exception {
		return ImageMagickImpl.getInstance().getGlobalSearchPath();
	}

	private String searchForCommandPath(String cmdName) throws Exception {
		String dirs[] = getGlobalSearchPath().split(File.pathSeparator);

		String cmdPath = null;

		for (String dir : dirs) {
			if (OSDetector.isWindows()) {
				File cmd = new File(dir, cmdName + ".exe");
				if (cmd.exists()) {
					cmdPath = cmd.getCanonicalPath();
					break;
				}

				cmd = new File(dir, cmdName + ".cmd");
				if (cmd.exists()) {
					cmdPath = cmd.getCanonicalPath();
					break;
				}

				cmd = new File(dir, cmdName + ".bat");
				if (cmd.exists()) {
					cmdPath = cmd.getCanonicalPath();
					break;
				}
			} else {
				File cmd = new File(dir, cmdName);
				if (cmd.exists()) {
					cmdPath = cmd.getCanonicalPath();
					break;
				}
			}
		}

		return cmdPath;
	}

	private static final String GHOSTSCRIPT_CMD_UNIX = "gs";
	private static final String GHOSTSCRIPT_CMD_WINDOWS_32 = "gswin32c";
	private static final String GHOSTSCRIPT_CMD_WINDOWS_64 = "gswin64c";

	private static Log _log = LogFactoryUtil.getLog(GhostScriptImpl.class);

	private String _commandPath;

}