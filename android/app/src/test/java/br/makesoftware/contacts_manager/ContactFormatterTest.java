package br.makesoftware.contacts_manager;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.makesoftware.contacts_manager.contacts.ContactFormatter;

public class ContactFormatterTest {
    private final String sampleContact = "11999999999";
    private final String sampleContactWithCountryCode = "+55" + sampleContact;

    @Test
    public void formatContactCorrectlyWhenWithCountryCode() {
        String formattedContact = ContactFormatter.formatContactPhone(sampleContactWithCountryCode);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWhenWithoutCountyCode() {
        String formattedContact = ContactFormatter.formatContactPhone(sampleContact);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithSpaces() {
        String formattedContact = ContactFormatter.formatContactPhone("11 99999 9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDots() {
        String formattedContact = ContactFormatter.formatContactPhone("11.99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDashes() {
        String formattedContact = ContactFormatter.formatContactPhone("11-99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParentheses() {
        String formattedContact = ContactFormatter.formatContactPhone("(11)99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndSpacesAndDashes() {
        String formattedContact = ContactFormatter.formatContactPhone("(11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDots() {
        String formattedContact = ContactFormatter.formatContactPhone("(11) 99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDashesAndSpacesAndDotsAndPlus() {
        String formattedContact = ContactFormatter.formatContactPhone("+55 (11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }
}
