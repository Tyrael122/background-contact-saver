package br.makesoftware.contacts_manager;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.makesoftware.contacts_manager.adapters.XmlApiAdapter;
import br.makesoftware.contacts_manager.constants.LogConstants;
import br.makesoftware.contacts_manager.interfaces.ApiAdapter;
import br.makesoftware.contacts_manager.utils.FileLogger;
import br.makesoftware.contacts_manager.utils.NotificationSender;

public class ContactWorker extends Worker {
    private FileLogger statusLogger = new FileLogger(getApplicationContext().getFilesDir(), LogConstants.STATUS_LOGGER_NAME);
    private FileLogger contactLogger = new FileLogger(getApplicationContext().getFilesDir(), LogConstants.CONTACT_LOGGER_NAME);
    private final ApiAdapter apiAdapter = new XmlApiAdapter(statusLogger);

    public ContactWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static boolean stopAllServices(Context context) {
        Operation operation = WorkManager.getInstance(context).cancelAllWork();

        try {
            operation.getResult().get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }

        return true;
    }

    @NonNull
    @Override
    public Result doWork() {
        statusLogger.logInfo("Tentando fazer uma requisição para a API.");

        List<String> contactsNotSent;
        try {
            contactsNotSent = apiAdapter.requestContactsNotSent();
        } catch (Exception e) {
            statusLogger.logError("Ocorreu um erro ao fazer uma requisição para a API: " + e.getMessage());
            return Result.failure();
        }

        statusLogger.logInfo("Foi feita uma requisição para a API.");
        if (contactsNotSent.isEmpty()) {
            statusLogger.logInfo("A requisição não trouxe nenhum contato.");
            return Result.success();
        }

        statusLogger.logInfo("A requisição trouxe " + contactsNotSent.size() + " contatos.");

        Result result = saveContacts(contactsNotSent);

        NotificationSender.sendNotification(getApplicationContext(), MainActivity.CHANNEL);

        return result;
    }

    private Result saveContacts(List<String> contactsNotSent) {
        boolean hasFailed = false;

        for (String contactPhone : contactsNotSent) {
            if (isContactSavedInPhone(contactPhone)) {
                contactLogger.logInfo("O contato " + contactPhone + " já está salvo no celular.");
                continue;
            }

            boolean hasContactBeenSaved = insertContact(contactPhone, contactPhone, getApplicationContext().getContentResolver());
            if (hasContactBeenSaved)
                contactLogger.logInfo("Contato " + contactPhone + " salvo com sucesso.");
            else {
                contactLogger.logInfo("Não foi possível salvar o contato '" + contactPhone + "'.");
                hasFailed = true;
            }
        }

        if (hasFailed) return Result.failure();
        else return Result.success();
    }

    private boolean isContactSavedInPhone(String contactPhone) {
        List<String> contacts = fetchAllContacts(getApplicationContext().getContentResolver());

        return contacts.contains(contactPhone);
    }

    private boolean insertContact(String name, String phoneNumber, ContentResolver contentResolver) {
        try {
            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
            builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
            builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
            operations.add(builder.build());

            // Set the contact's name
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            operations.add(builder.build());

            // Set the contact's phone number
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
            operations.add(builder.build());

            ContentProviderResult[] results = contentResolver.applyBatch(ContactsContract.AUTHORITY, operations);

            return results != null && results.length > 0;
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> fetchAllContacts(ContentResolver contentResolver) {
        List<String> contactsList = new ArrayList<>();

        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)) {

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactsList.add(displayName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactsList;
    }
}
