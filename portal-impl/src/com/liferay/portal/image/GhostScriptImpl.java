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
import com.liferay.portal.kernel.image.ImageMagickUtil;
import com.liferay.portal.kernel.process.ProcessExecutor;
import com.liferay.portal.kernel.util.OSDetector;

import java.io.File;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Ivica Cardic
 */
public class GhostScriptImpl implements GhostScript {

	public Future convert(List<String> arguments) throws Exception {

		if (!ImageMagickUtil.isEnabled()) {
			throw new IllegalStateException(
				"Cannot call \"" + getGhostScriptCommand() +
					"\" when GhostScript is disabled. Enable ImageMagick " +
					"along with GhostScript.");
		}

		LinkedList<String> commands = new LinkedList<String>();

		commands.add(getGhostScriptCommandPath());
		commands.add("-dBATCH");
		commands.add("-dSAFER");
		commands.add("-dNOPAUSE");
		commands.add("-dNOPROMPT");
		commands.add("-sFONTPATH" + ImageMagickUtil.getGlobalSearchPath());

		commands.addAll(arguments);

		return ProcessExecutor.execute(commands, null);
	}

	private String getGhostScriptCommand() {
		if (OSDetector.isWindows()) {
			return OSDetector.getBitmode().equals("32") ?
				GHOSTSCRIPT_WINDOWS_32 : GHOSTSCRIPT_WINDOWS_64;
		}else {
			return GHOSTSCRIPT_UNIX;
		}
	}

	private String getGhostScriptCommandPath() throws Exception {
		if (_ghostScriptCommand == null) {
			String ghostScriptCommand = getGhostScriptCommand();

			String dirs[] = ImageMagickUtil.getGlobalSearchPath().split(
				File.pathSeparator);

			for (int i = 0; i < dirs.length; ++i) {
				if (OSDetector.isWindows()) {
					// try thre typical extensions
					File cmd = new File(dirs[i], ghostScriptCommand + ".exe");
					if (cmd.exists()) {
						_ghostScriptCommand = cmd.getCanonicalPath();
						break;
					}

					cmd = new File(dirs[i], ghostScriptCommand + ".cmd");
					if (cmd.exists()) {
						_ghostScriptCommand = cmd.getCanonicalPath();
						break;
					}

					cmd = new File(dirs[i], ghostScriptCommand + ".bat");
					if (cmd.exists()) {
						_ghostScriptCommand = cmd.getCanonicalPath();
						break;
					}
				} else {
					File cmd = new File(dirs[i], ghostScriptCommand);
					if (cmd.exists()) {
						_ghostScriptCommand = cmd.getCanonicalPath();
						break;
					}
				}
			}
		}

		return _ghostScriptCommand;
	}

	private static final String GHOSTSCRIPT_UNIX = "gs";
	private static final String GHOSTSCRIPT_WINDOWS_32 = "gswin32c";
	private static final String GHOSTSCRIPT_WINDOWS_64 = "gswin64c";

	private String _ghostScriptCommand;

}