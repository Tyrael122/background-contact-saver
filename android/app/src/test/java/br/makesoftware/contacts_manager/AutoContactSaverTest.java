package br.makesoftware.contacts_manager;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import br.makesoftware.contacts_manager.contacts.ContactRepositoryMock;
import br.makesoftware.contacts_manager.contacts.PhoneNumberFormatter;
import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.Logger;
import br.makesoftware.contacts_manager.services.AutoContactSaver;

public class AutoContactSaverTest {

    private static ConcernedPeopleNotifier concernedPeopleNotifier;
    private final ContactRepositoryMock contactRepository = new ContactRepositoryMock();
    private final String contactDisplayPhone = "12 98317-4013";
    private final String formattedDisplayPhone = PhoneNumberFormatter.removeNonDigits(contactDisplayPhone);

    private final String formattedContactPhone = "+5512983174013";
    private final String formattedContactPhoneWithoutNine = "+551283174013";

    @BeforeClass
    public static void setUp() {
        concernedPeopleNotifier = mock(ConcernedPeopleNotifier.class);
        Logger.shouldLogToFile(false);
    }

    @Test
    public void testSavePendingContacts() throws Exception {
        ContactApiAdapter contactApiAdapterMock = createContactApiAdapterMock(List.of(contactDisplayPhone));

        AutoContactSaver autoContactSaver = new AutoContactSaver(contactApiAdapterMock, contactRepository, concernedPeopleNotifier);
        autoContactSaver.savePendingContacts();

        assertTrue(contactRepository.isContactSavedInPhone(formattedDisplayPhone, formattedContactPhone, formattedContactPhoneWithoutNine));
    }

    @Test
    public void testDontSaveAlreadySavedContacts() throws Exception {
        ContactApiAdapter contactApiAdapterMock = createContactApiAdapterMock(List.of(contactDisplayPhone));

        AutoContactSaver autoContactSaver = new AutoContactSaver(contactApiAdapterMock, contactRepository, concernedPeopleNotifier);
        autoContactSaver.savePendingContacts();
        autoContactSaver.savePendingContacts();

        assertEquals(1, contactRepository.getNumberOfMatches(formattedDisplayPhone, formattedContactPhone, formattedContactPhoneWithoutNine));
    }

    @NonNull
    private static ContactApiAdapter createContactApiAdapterMock(List<String> contacts) throws Exception {
        ContactApiAdapter contactApiAdapterMock = mock(ContactApiAdapter.class);
        when(contactApiAdapterMock.requestContactsNotSent()).thenReturn(contacts);

        return contactApiAdapterMock;
    }
}
