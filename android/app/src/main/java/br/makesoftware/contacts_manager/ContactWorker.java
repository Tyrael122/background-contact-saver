package br.makesoftware.contacts_manager;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

public class ContactWorker extends Worker {
    public ContactWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        System.out.println("Trying to make a request to the API.");
        // TODO: Make the API request.
        List<String> contactsNotSent = List.of("222222222");

        System.out.println("Foi feita uma requisição para a API.");
        if (contactsNotSent.isEmpty()) {
            System.out.println("A requisição não trouxe nenhum contato.");
            return Result.success();
        }

        System.out.println("A requisição trouxe " + contactsNotSent.size() + "contatos.");
        return saveContacts(contactsNotSent);
    }

    private Result saveContacts(List<String> contactsNotSent) {
        for (String contactPhone : contactsNotSent) {
            if (isContactSavedInPhone(contactPhone)) {
                System.out.println("O contato " + contactPhone + " já está salvo no celular.");
                continue;
            }

            boolean hasContactBeenSaved = insertContact(contactPhone, contactPhone, getApplicationContext().getContentResolver());
            if (hasContactBeenSaved) System.out.println("Contato " + contactPhone + " salvo com sucesso.");
            else {
                System.out.println("Não foi possível salvar o contato '" + contactPhone + "'.");
            }
        }

        return Result.success(); // TODO: Tell whether some contact couldn't be saved.
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
