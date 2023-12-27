package br.makesoftware.contacts_manager.contacts;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import br.makesoftware.contacts_manager.interfaces.ContactRepository;

public class ContactRepositoryMock implements ContactRepository {
    private final List<ContactData> contacts = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean isContactSavedInPhone(String contactDisplayPhone) {
        List<String> contacts = fetchAllContactDisplayNames();

        return contacts.contains(contactDisplayPhone);
    }

    @Override
    public List<String> fetchAllContactDisplayNames() {
        List<String> contacts = new ArrayList<>();

        for (ContactData contact : this.contacts) {
            contacts.add(contact.getDisplayName());
        }

        return contacts;
    }

    @Override
    public void saveContact(String contactDisplayName, String... contactPhoneNumbers) {
        contacts.add(new ContactData(contactDisplayName, List.of(contactPhoneNumbers)));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean isContactSavedInPhone(String contactDisplayName, String... contactPhoneNumbers) {
        return getNumberOfMatches(contactDisplayName, contactPhoneNumbers) >= 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getNumberOfMatches(String contactDisplayName, String... contactPhoneNumbers) {
        return contacts.stream().filter(contact -> contact.getDisplayName().equals(contactDisplayName) && contact.getPhoneNumbers().equals(List.of(contactPhoneNumbers))).count();
    }

    public static class ContactData {
        private final String displayName;
        private final List<String> phoneNumbers;

        public ContactData(String displayName, List<String> phoneNumbers) {
            this.displayName = displayName;
            this.phoneNumbers = phoneNumbers;
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<String> getPhoneNumbers() {
            return phoneNumbers;
        }
    }
}
