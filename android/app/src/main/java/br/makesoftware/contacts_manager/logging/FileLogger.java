package br.makesoftware.contacts_manager.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.makesoftware.contacts_manager.constants.LogType;

public class FileLogger {
    private static File filesDir;
    private static boolean shouldLog = true;

    private FileLogger() {}

    public static void initialize(File filesDir) {
        FileLogger.filesDir = filesDir;
    }

    public static void shouldLog(boolean shouldLog) {
        FileLogger.shouldLog = shouldLog;
    }

    public static void logError(String message, LogType logType) {
        logToFile(message, Level.SEVERE, logType.toString());
    }

    public static void logInfo(String message, LogType logType) {
        logToFile(message, Level.INFO, logType.toString());
    }

    public static void logDebug(String message, LogType logType) {
        logToFile(message, Level.FINE, logType.toString());
    }

    private static void logToFile(String message, Level logLevel, String logFilename) {
        if (!shouldLog) return;

        Logger logger = Logger.getAnonymousLogger();

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
