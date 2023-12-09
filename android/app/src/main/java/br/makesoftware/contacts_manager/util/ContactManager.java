package br.makesoftware.contacts_manager.util;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private static final String REGEX_SOMENTE_NUMEROS = "[^0-9]";
    private final ContentResolver contentResolver;

    public ContactManager(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public boolean isContactSavedInPhone(String contactPhone) {
        List<String> contacts = fetchAllContacts();

        return contacts.contains(contactPhone);
    }

    public List<String> fetchAllContacts() {
        List<String> contactsList = new ArrayList<>();

        try (Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)) {

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactsList.add(displayName);
                }
            }
        }

        return contactsList;
    }

    public static String formatContactPhone(String contactPhone) {
        contactPhone = contactPhone.replaceAll(REGEX_SOMENTE_NUMEROS, "");

        if (contactPhone.startsWith("55")) {
            contactPhone = "+" + contactPhone;
        } else {
            contactPhone = "+55" + contactPhone;
        }

        return contactPhone;
    }

    public boolean insertContact(String name, String phoneNumber) throws RemoteException, OperationApplicationException {
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
    }
}
