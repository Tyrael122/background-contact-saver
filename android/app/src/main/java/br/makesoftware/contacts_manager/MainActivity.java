package br.makesoftware.contacts_manager;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import br.makesoftware.contacts_manager.constants.LogConstants;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "br.makesoftware.contacts_manager/channel";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler((call, result) -> {
            switch (call.method) {
                case "startService":
                    int repeatInterval = call.argument("requestInterval");
                    PeriodicWorkRequest contactsWorkRequest = new PeriodicWorkRequest.Builder(ContactWorker.class, repeatInterval, TimeUnit.MINUTES).build();

                    WorkManager.getInstance(getApplicationContext()).enqueue(contactsWorkRequest);

                    result.success(true);
                    break;
                case "stopService":
                    boolean hasStoppedSucessfully = ContactWorker.stopAllServices(getApplicationContext());
                    result.success(hasStoppedSucessfully);
                    break;
                case "logError":
                    new FileLogger(getApplicationContext().getFilesDir(), LogConstants.STATUS_LOGGER_NAME).logError(call.argument("message"));
                    break;
                case "logInfo":
                    new FileLogger(getApplicationContext().getFilesDir(), LogConstants.STATUS_LOGGER_NAME).logInfo(call.argument("message"));
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }
}


