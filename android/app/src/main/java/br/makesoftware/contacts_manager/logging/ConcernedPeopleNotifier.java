package br.makesoftware.contacts_manager.logging;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import br.makesoftware.contacts_manager.constants.LogType;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConcernedPeopleNotifier {
    private final Context applicationContext;

    public ConcernedPeopleNotifier(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void sendErrorMessage(String message) {
        FileLogger.logError(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification("Houve falha ao executar o serviço em segundo plano.\n" + message, applicationContext);
    }

    public void sendInfoMessage(String message) {
        FileLogger.logInfo(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification("O serviço em segundo plano foi executado.\n" + message, applicationContext);
    }
}
