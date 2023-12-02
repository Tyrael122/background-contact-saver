package br.makesoftware.contacts_manager.services;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.makesoftware.contacts_manager.adapters.XmlApiAdapter;
import br.makesoftware.contacts_manager.constants.LogType;
import br.makesoftware.contacts_manager.interfaces.ApiAdapter;
import br.makesoftware.contacts_manager.utils.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.utils.FileLogger;

public class AutoContactSaver {
    private final ConcernedPeopleNotifier concernedPeopleNotifier;
    private final ApiAdapter apiAdapter;

    private final Context context;

    public AutoContactSaver(Context context) {
        this.context = context;
        
        concernedPeopleNotifier = new ConcernedPeopleNotifier(context);
        apiAdapter =  new XmlApiAdapter();
    }

    public boolean savePendingContacts() {
        FileLogger.logInfo("Tentando fazer uma requisição para a API.", LogType.STATUS);

        boolean actionResult;

        List<String> contactsNotSent;
        try {
            contactsNotSent = apiAdapter.requestContactsNotSent();
        } catch (Exception e) {
            String errorMessage = "Ocorreu um erro ao fazer uma requisição para a API: " + e.getMessage();
            concernedPeopleNotifier.sendErrorMessage(errorMessage);

            return false;
        }

        FileLogger.logInfo("Foi feita uma requisição para a API.", LogType.STATUS);

        String infoMessage;
        if (contactsNotSent.isEmpty()) {
            infoMessage = "A requisição não trouxe nenhum contato.";
            actionResult = true;

        } else {
            infoMessage = "A requisição trouxe " + contactsNotSent.size() + " contatos.";
            actionResult = saveContacts(contactsNotSent);
        }

        concernedPeopleNotifier.sendInfoMessage(infoMessage);

        return actionResult;
    }

    private boolean saveContacts(List<String> contactsNotSent) {
        boolean hasSucceeded = true;

        for (String contactPhone : contactsNotSent) {
            if (isContactSavedInPhone(contactPhone)) {
                FileLogger.logInfo("O contato " + contactPhone + " já está salvo no celular.", LogType.CONTACT);
                continue;
            }

            boolean hasContactBeenSaved = insertContact(contactPhone, contactPhone, context.getContentResolver());
            if (hasContactBeenSaved)
                FileLogger.logInfo("Contato " + contactPhone + " salvo com sucesso.", LogType.CONTACT);
            else {
                FileLogger.logInfo("Não foi possível salvar o contato '" + contactPhone + "'.", LogType.CONTACT);
                hasSucceeded = false;
            }
        }

        return hasSucceeded;
    }

    private boolean isContactSavedInPhone(String contactPhone) {
        List<String> contacts = fetchAllContacts(context.getContentResolver());

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

            return results.length > 0;
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
