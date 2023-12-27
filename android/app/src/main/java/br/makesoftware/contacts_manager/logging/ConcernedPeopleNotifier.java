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
        Logger.logError(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification("Houve falha ao executar o serviço.\n" + message, applicationContext);
    }

    public void sendInfoMessage(String message) {
        Logger.logInfo(message, LogType.STATUS);

        NotificationSender.sendSimpleNotification(message, applicationContext);
    }
}
