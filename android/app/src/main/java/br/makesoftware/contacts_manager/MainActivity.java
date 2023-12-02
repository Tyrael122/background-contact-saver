package br.makesoftware.contacts_manager;

import static br.makesoftware.contacts_manager.constants.LogType.STATUS;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import br.makesoftware.contacts_manager.services.ForegroundContactService;
import br.makesoftware.contacts_manager.utils.FileLogger;
import br.makesoftware.contacts_manager.utils.NotificationSender;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    public static final String CHANNEL = "br.makesoftware.contacts_manager/channel";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationSender.createAllNotificationChannels(getApplicationContext());

        FileLogger.initialize(getApplicationContext().getFilesDir());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler((call, result) -> {
            switch (call.method) {
                case "startService":
                    boolean success = tryStartPeriodicService(call);
                    result.success(success);
                    break;

                case "stopService":
                    boolean hasStoppedSucessfully = tryStopService();

                    result.success(hasStoppedSucessfully);
                    break;

                case "logError":
                    FileLogger.logError(call.argument("message"), STATUS);
                    break;

                case "logInfo":
                    FileLogger.logInfo(call.argument("message"), STATUS);
                    break;

                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean tryStartPeriodicService(MethodCall call) {
        try {
            int repeatInterval = call.argument("requestInterval");
            ForegroundContactService.setRequestIntervalMinutes(repeatInterval);

            intent = new Intent(this, ForegroundContactService.class);
            getApplicationContext().startForegroundService(intent);

            return true;

        } catch (Exception e) {
            FileLogger.logError(e.getMessage(), STATUS);

            return false;
        }
    }

    private boolean tryStopService() {
        boolean hasStoppedSucessfully = true;
        try {
            if (intent != null) {
                getApplicationContext().stopService(intent);
            }

        } catch (Exception e) {
            hasStoppedSucessfully = false;

            FileLogger.logError(e.getMessage(), STATUS);
        }

        return hasStoppedSucessfully;
    }
}


