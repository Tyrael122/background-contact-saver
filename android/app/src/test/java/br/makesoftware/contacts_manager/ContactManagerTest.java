package br.makesoftware.contacts_manager;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.makesoftware.contacts_manager.util.ContactManager;

public class ContactManagerTest {
    private final String sampleContact = "11999999999";
    private final String sampleContactWithCountryCode = "+55" + sampleContact;

    @Test
    public void formatContactCorrectlyWhenWithCountryCode() {
        String formattedContact = ContactManager.formatContactPhone(sampleContactWithCountryCode);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWhenWithoutCountyCode() {
        String formattedContact = ContactManager.formatContactPhone(sampleContact);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithSpaces() {
        String formattedContact = ContactManager.formatContactPhone("11 99999 9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDots() {
        String formattedContact = ContactManager.formatContactPhone("11.99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDashes() {
        String formattedContact = ContactManager.formatContactPhone("11-99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParentheses() {
        String formattedContact = ContactManager.formatContactPhone("(11)99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndSpacesAndDashes() {
        String formattedContact = ContactManager.formatContactPhone("(11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDots() {
        String formattedContact = ContactManager.formatContactPhone("(11) 99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDashesAndSpacesAndDotsAndPlus() {
        String formattedContact = ContactManager.formatContactPhone("+55 (11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }
}
