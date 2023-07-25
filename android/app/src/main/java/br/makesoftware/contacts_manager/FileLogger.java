package br.makesoftware.contacts_manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class FileLogger {
    private final Logger logger;

    public FileLogger(File filesDir, String loggerName) {
        logger = Logger.getLogger(loggerName);

        setLoggerFile(logger, filesDir, loggerName);

        logger.setUseParentHandlers(false);
    }

    public void logError(String message) {
        logger.severe(message);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    private void setLoggerFile(Logger logger, File filesDir, String loggerName) {
        FileHandler fh;
        try {
            boolean appendToLogFile = false;

            String logDir = filesDir.getAbsolutePath() + "/logs/";
            createLogDirectoryIfNotExists(logDir);

            String fileName = loggerName + ".txt";

            System.out.println("Logger file path: " + logDir + fileName);

            fh = new FileHandler(logDir + fileName, appendToLogFile);
            fh.setFormatter(new CustomLogFormatter());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.addHandler(fh);
    }

    private static void createLogDirectoryIfNotExists(String filesDir) throws IOException {
        File logDir = new File(filesDir);
        if (!logDir.exists())
            logDir.mkdir();
    }
}
