package br.makesoftware.contacts_manager.logging;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import br.makesoftware.contacts_manager.constants.LogType;

public class Logger {
    // TODO: Refactor the file logging to a separate class (FileLogger).
    private static File filesDir;
    private static boolean shouldLogToFile = true;
    private static boolean shouldLogToLogCat = false;

    private Logger() {
    }

    public static void initialize(File filesDir) {
        Logger.filesDir = filesDir;
    }

    public static void shouldLogToFile(boolean shouldLogToFile) {
        Logger.shouldLogToFile = shouldLogToFile;
    }

    public static void logError(String message, LogType logType) {
        log(message, Level.SEVERE, logType);
    }

    public static void logInfo(String message, LogType logType) {
        log(message, Level.INFO, logType);
    }

    public static void logDebug(String message, LogType logType) {
        log(message, Level.FINE, logType);
    }

    private static void log(String message, Level logLevel, LogType logType) {
        System.out.println(logLevel + ": " + logType + ": " + message);

        logToLogCat(logLevel, logType.toString(), message);
        logToFile(message, logLevel, logType.toString());
    }

    private static void logToLogCat(Level logLevel, String tag, String message) {
        if (!shouldLogToLogCat) return;

        Log.println(logLevel.intValue(), tag, message);
    }

    private static void logToFile(String message, Level logLevel, String logFilename) {
        if (!shouldLogToFile) return;
        if (filesDir == null)
            throw new IllegalStateException("The directory which to write the log file haven't been initialized.");

        java.util.logging.Logger logger = java.util.logging.Logger.getAnonymousLogger();

        FileHandler fh = getFileHandlerToLogToFile(logFilename);
        logger.addHandler(fh);

        logger.log(logLevel, message);

        fh.close();
    }

    private static FileHandler getFileHandlerToLogToFile(String logFilename) {
        FileHandler fh;
        try {
            boolean appendToLogFile = true;

            String logDir = filesDir.getAbsolutePath() + "/logs/";
            createLogDirectoryIfNotExists(logDir);

            String fileName = logFilename + ".txt";

            fh = new FileHandler(logDir + fileName, appendToLogFile);
            fh.setFormatter(new CustomLogFormatter());

            return fh;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createLogDirectoryIfNotExists(String filesDir) throws IOException {
        File logDir = new File(filesDir);
        if (!logDir.exists()) logDir.mkdir();
    }
}
