package br.makesoftware.contacts_manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class FileLogger {
    private final Logger logger;

    public FileLogger(File filesDir, String loggerName) {
        logger = Logger.getLogger(loggerName);

        setLogFile(filesDir, loggerName);

//        setLoggerToNotLogToConsole();
    }

    public void logError(String message) {
        logger.severe(message);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    private void setLogFile(File filesDir, String loggerName) {
        FileHandler fh;
        try {
            boolean appendToLogFile = true;

            String logDir = filesDir.getAbsolutePath() + "/logs/";
            createLogDirectoryIfNotExists(logDir);

            String fileName = loggerName + ".txt";

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

    private void setLoggerToNotLogToConsole() {
        logger.setUseParentHandlers(false);
    }

}
