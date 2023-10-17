package br.makesoftware.contacts_manager.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.makesoftware.contacts_manager.constants.LogType;

public class FileLogger {
    private final File filesDir;

    public FileLogger(File filesDir) {
        this.filesDir = filesDir;
    }

    public void logError(String message, LogType logType) {
        logToFile(message, Level.SEVERE, logType.toString());
    }

    public void logInfo(String message, LogType logType) {
        logToFile(message, Level.INFO, logType.toString());
    }

    private void logToFile(String message, Level logLevel, String logFilename) {
        Logger logger = Logger.getAnonymousLogger();

        FileHandler fh = getFileHandlerToLogToFile(logFilename);
        logger.addHandler(fh);

        logger.log(logLevel, message);

        fh.close();
    }

    private FileHandler getFileHandlerToLogToFile(String logFilename) {
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
