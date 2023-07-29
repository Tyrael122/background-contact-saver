package br.makesoftware.contacts_manager.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import br.makesoftware.contacts_manager.utils.CustomLogFormatter;

public class FileLogger {
    private final Logger logger;
    private final File filesDir;

    public FileLogger(File filesDir, String loggerName) {
        this.filesDir = filesDir;

        logger = Logger.getLogger(loggerName);

//        setLoggerToNotLogToConsole();
    }

    public void logError(String message) {
        logToFile(() -> logger.severe(message));
    }

    public void logInfo(String message) {
        logToFile(() -> logger.info(message));
    }

    public void logToFile(Runnable loggerFunction) {
        FileHandler fh = getFileHandlerToLogToFile();
        logger.addHandler(fh);

        loggerFunction.run();

        fh.close();
    }

    private FileHandler getFileHandlerToLogToFile() {
        FileHandler fh;
        try {
            boolean appendToLogFile = true;

            String logDir = filesDir.getAbsolutePath() + "/logs/";
            createLogDirectoryIfNotExists(logDir);

            String fileName = logger.getName() + ".txt";

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

    private void setLoggerToNotLogToConsole() {
        logger.setUseParentHandlers(false);
    }
}
