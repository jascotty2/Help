/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: Logging helper for Help
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.help.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelpLogger extends Logger {

	protected final static Logger _logger = Logger.getLogger("Minecraft");
	protected final static Logger logger = new HelpLogger();
	protected final static String logFormat = "[Help] %s";

	HelpLogger() {
		//super("Minecraft", null);
		super(null, null);
	}

	@Override
	public synchronized void log(Level level, String message, Object param) {
		//super.log(level, message == null ? null : String.format(logFormat, message), param);
		//_logger.log(level, message == null ? null : String.format(logFormat, message), param);
		Log(level, message, param);
	}

	@Override
	public synchronized void log(Level level, String message, Object[] params) {
		//super.log(level, message == null ? null : String.format(logFormat, message), params);
		//_logger.log(level, message == null ? null : String.format(logFormat, message), params);
		Log(level, message, params, true);
	}

	@Override
	public synchronized void log(Level level, String message, Throwable thrown) {
		//super.log(level, message == null ? null : String.format(logFormat, message), thrown);
		//_logger.log(level, message == null ? null : String.format(logFormat, message), thrown);
		Log(level, message, thrown);
	}

	@Override
	public synchronized void severe(String msg) {
		Log(Level.SEVERE, msg, null, true);
	}

	@Override
	public synchronized void warning(String msg) {
		Log(Level.WARNING, msg, null, true);
	}

	@Override
	public synchronized void info(String msg) {
		Log(Level.INFO, msg, null, true);
	}

	@Override
	public synchronized void config(String msg) {
		_logger.config(msg);
	}

	@Override
	public synchronized void fine(String msg) {
		Log(Level.FINE, msg, null, true);
	}

	@Override
	public synchronized void finer(String msg) {
		Log(Level.FINER, msg, null, true);
	}

	@Override
	public synchronized void finest(String msg) {
		Log(Level.FINEST, msg, null, true);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void Fine(String msg) {
		Log(Level.FINE, msg, null, true);
	}

	public static void Info(String msg) {
		Log(Level.INFO, msg, (Throwable) null);
	}

	public static void Warning(String msg) {
		Log(Level.WARNING, msg, null, true);
	}

	public static void Warning(Exception err) {
		Log(Level.WARNING, null, err);
	}

	public static void Warning(String msg, Throwable err) {
		Log(Level.WARNING, msg, err);
	}

	public static void Severe(String msg) {
		Log(Level.SEVERE, msg, null);
	}

	public static void Severe(Exception err) {
		Log(Level.SEVERE, null, err);
	}

	public static void Severe(String msg, Throwable err) {
		Log(Level.SEVERE, msg, err);
	}

	public static void Log(String msg) {
		Log(Level.INFO, msg, null);
	}

	public static void Log(String msg, Throwable err) {
		Log(Level.INFO, msg, err);
	}

	public static void Log(Level loglevel, String msg) {
		Log(loglevel, msg, null);
	}

	public static void Log(Level loglevel, Exception err) {
		Log(loglevel, null, err);
	}

	public static void Log(Level loglevel, Throwable err) {
		Log(loglevel, null, err);
	}

	public static void Log(Level loglevel, String msg, Object params) {
		if (params != null && params instanceof Throwable) {
			Throwable err = (Throwable) params;
			if (msg == null) {
				_logger.log(loglevel, String.format(logFormat,
						err == null ? "? unknown exception ?" : err.getMessage()), err);
			} else {
				_logger.log(loglevel, String.format(logFormat, msg), err);
			}
		} else if (msg == null) {
			_logger.log(loglevel, String.format(logFormat), params);
		} else {
			_logger.log(loglevel, String.format(logFormat, msg), params);
		}
	}

	public static void Log(Level loglevel, String msg, Object[] params, boolean sendReport) {
		if (msg == null) {
			_logger.log(loglevel, String.format(logFormat), params);
		} else {
			_logger.log(loglevel, String.format(logFormat, msg), params);
		}
	}

}

