package br.makesoftware.contacts_manager;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import br.makesoftware.contacts_manager.contacts.ContactFormatter;
import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.FileLogger;
import br.makesoftware.contacts_manager.contacts.ContactRepositoryMock;
import br.makesoftware.contacts_manager.services.AutoContactSaver;

public class AutoContactSaverTest {

    private static ConcernedPeopleNotifier concernedPeopleNotifier;
    private final ContactRepositoryMock contactRepository = new ContactRepositoryMock();
    private final String contactDisplayPhone = "12 983174013";
    private final String formattedDisplayPhone = ContactFormatter.removeSpaces(contactDisplayPhone);

    private final String formattedContactPhone = "+5512983174013";

    @BeforeClass
    public static void setUp() {
        concernedPeopleNotifier = mock(ConcernedPeopleNotifier.class);
        FileLogger.shouldLog(false);
    }

    @Test
    public void testSavePendingContacts() throws Exception {
        ContactApiAdapter contactApiAdapterMock = createContactApiAdapterMock(List.of(contactDisplayPhone));

        AutoContactSaver autoContactSaver = new AutoContactSaver(contactApiAdapterMock, contactRepository, concernedPeopleNotifier);
        autoContactSaver.savePendingContacts();

        assertTrue(contactRepository.isContactSavedInPhone(formattedDisplayPhone, formattedContactPhone));
    }

    @Test
    public void testDontSaveAlreadySavedContacts() throws Exception {
        ContactApiAdapter contactApiAdapterMock = createContactApiAdapterMock(List.of(contactDisplayPhone));

        AutoContactSaver autoContactSaver = new AutoContactSaver(contactApiAdapterMock, contactRepository, concernedPeopleNotifier);
        autoContactSaver.savePendingContacts();
        autoContactSaver.savePendingContacts();

        assertEquals(1, contactRepository.getNumberOfMatches(contact -> contact.getDisplayName().equals(formattedDisplayPhone) && contact.getPhoneNumber().equals(formattedContactPhone)));
    }

    @NonNull
    private static ContactApiAdapter createContactApiAdapterMock(List<String> contacts) throws Exception {
        ContactApiAdapter contactApiAdapterMock = mock(ContactApiAdapter.class);
        when(contactApiAdapterMock.requestContactsNotSent()).thenReturn(contacts);

        return contactApiAdapterMock;
    }
}
