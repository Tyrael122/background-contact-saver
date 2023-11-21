package br.makesoftware.contacts_manager.utils;

import android.content.Context;

import br.makesoftware.contacts_manager.constants.LogType;

public class ConcernedPeopleNotifier {
    private final FileLogger statusLogger;
    private final Context applicationContext;

    public ConcernedPeopleNotifier(FileLogger fileLogger, Context applicationContext) {
        this.statusLogger = fileLogger;
        this.applicationContext = applicationContext;
    }

    public void sendErrorMessage(String message) {
        statusLogger.logError(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification("Houve falha ao executar o serviço em segundo plano.\n" + message, applicationContext);
    }

    public void sendInfoMessage(String message) {
        statusLogger.logInfo(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification("O serviço em segundo plano foi executado.\n" + message, applicationContext);
    }
}
