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
    public void saveContact(String contactDisplayName, String contactPhoneNumber) {
        contacts.add(new ContactData(contactDisplayName, contactPhoneNumber));
    }

    public List<ContactData> fetchAllContacts() {
        return contacts;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean isContactSavedInPhone(String contactDisplayName, String contactPhoneNumber) {
        return contacts.stream().anyMatch(contact -> contact.getDisplayName().equals(contactDisplayName) && contact.getPhoneNumber().equals(contactPhoneNumber));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long getNumberOfMatches(Predicate<ContactData> predicate) {
        return contacts.stream().filter(predicate).count();
    }

    public static class ContactData {
        private final String displayName;
        private final String phoneNumber;

        public ContactData(String displayName, String phoneNumber) {
            this.displayName = displayName;
            this.phoneNumber = phoneNumber;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
