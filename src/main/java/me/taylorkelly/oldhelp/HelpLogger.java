package me.taylorkelly.oldhelp;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelpLogger {

    public static final Logger logger = Logger.getLogger("Minecraft");

    public static void severe(String string, Exception ex) {
        logger.log(Level.SEVERE, "[HELP] " + string, ex);
    }

    public static void severe(String string) {
        logger.log(Level.SEVERE, "[HELP] ".concat(string));
    }

    public static void info(String string) {
        logger.log(Level.INFO, "[HELP] ".concat(string));
    }

    public static void warning(String string) {
        logger.log(Level.WARNING, "[HELP] ".concat(string));
    }
    
    public static void Log(String txt) {
        logger.log(Level.INFO, String.format("[HELP] %s", txt));
    }

    public static void Log(Level loglevel, String txt) {
        Log(loglevel, txt, true);
    }

    public static void Log(Level loglevel, String txt, boolean sendReport) {
        logger.log(loglevel, String.format("[HELP] %s", txt == null ? "" : txt));
    }

    public static void Log(Level loglevel, String txt, Exception params) {
        if (txt == null) {
            Log(loglevel, params);
        } else {
            logger.log(loglevel, String.format("[HELP] %s", txt == null ? "" : txt), (Exception) params);
        }
    }

    public static void Log(Level loglevel, Exception err) {
        logger.log(loglevel, String.format("[HELP] %s", err == null ? "? unknown exception ?" : err.getMessage()), err);
    }
}
