package br.makesoftware.contacts_manager.constants;

import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public enum NotificationChannel {
    ONGOING("Serviço", "Notificação permanente do serviço", NotificationManager.IMPORTANCE_DEFAULT),
    ALERT("Alertas", "Alertas gerais do aplicativo", NotificationManager.IMPORTANCE_DEFAULT);

    private final String name;
    private final String description;
    private final int importance;

    NotificationChannel(String name, String description, int importance) {
        this.name = name;
        this.description = description;
        this.importance = importance;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImportance() {
        return importance;
    }
}
