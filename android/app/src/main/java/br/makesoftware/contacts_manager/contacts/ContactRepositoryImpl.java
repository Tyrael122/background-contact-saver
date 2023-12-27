package br.makesoftware.contacts_manager.contacts;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import br.makesoftware.contacts_manager.interfaces.ContactRepository;

public class ContactRepositoryImpl implements ContactRepository {

    private final ContentResolver contentResolver;

    public ContactRepositoryImpl(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean isContactSavedInPhone(String contactDisplayPhone) {
        List<String> contacts = fetchAllContactDisplayNames();

        return contacts.contains(contactDisplayPhone);
    }

    @Override
    public List<String> fetchAllContactDisplayNames() {
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

    @Override
    public void saveContact(String contactDisplayName, String... contactPhoneNumbers) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(builder.build());

        // Set the contact's name
        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactDisplayName);
        operations.add(builder.build());

        for (String phoneNumber : contactPhoneNumbers) {
            // Set the contact's phone number
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
            operations.add(builder.build());
        }

        contentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
    }
}
