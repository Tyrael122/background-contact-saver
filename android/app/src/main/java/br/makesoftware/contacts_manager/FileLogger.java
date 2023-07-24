package br.makesoftware.contacts_manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {
    private final Logger logger;

    public FileLogger(File filesDir, String loggerName) {
        logger = Logger.getLogger(loggerName);

        setLoggerToLogToFile(logger, filesDir, loggerName);
    }

    public void logError(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    private void setLoggerToLogToFile(Logger logger, File filesDir, String loggerName) {
        FileHandler fh;
        try {
            boolean appendToLogFile = true;

            String fileName = loggerName + ".txt";
            fh = new FileHandler(filesDir.getAbsolutePath() + "/" + fileName, appendToLogFile);
            fh.setFormatter(new SimpleFormatter());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.addHandler(fh);
    }
}
