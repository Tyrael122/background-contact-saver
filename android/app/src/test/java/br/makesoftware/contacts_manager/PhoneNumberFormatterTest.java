package br.makesoftware.contacts_manager;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.makesoftware.contacts_manager.contacts.PhoneNumberFormatter;

public class PhoneNumberFormatterTest {
    private final String sampleContact = "11999999999";
    private final String sampleContactWithCountryCode = "+55" + sampleContact;

    @Test
    public void formatContactCorrectlyWhenWithCountryCode() {
        String formattedContact = PhoneNumberFormatter.formatPhone(sampleContactWithCountryCode);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWhenWithoutCountyCode() {
        String formattedContact = PhoneNumberFormatter.formatPhone(sampleContact);

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithSpaces() {
        String formattedContact = PhoneNumberFormatter.formatPhone("11 99999 9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDots() {
        String formattedContact = PhoneNumberFormatter.formatPhone("11.99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithDashes() {
        String formattedContact = PhoneNumberFormatter.formatPhone("11-99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParentheses() {
        String formattedContact = PhoneNumberFormatter.formatPhone("(11)99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndSpacesAndDashes() {
        String formattedContact = PhoneNumberFormatter.formatPhone("(11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDots() {
        String formattedContact = PhoneNumberFormatter.formatPhone("(11) 99999.9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void formatContactCorrectlyWithParenthesesAndDashesAndSpacesAndDotsAndPlus() {
        String formattedContact = PhoneNumberFormatter.formatPhone("+55 (11) 99999-9999");

        assertEquals(sampleContactWithCountryCode, formattedContact);
    }

    @Test
    public void removeNineCorrectlyWhenSuitablePhone() {
        String phoneWithoutNine = PhoneNumberFormatter.removeNine("+5511988888888");

        assertEquals("+551188888888", phoneWithoutNine);
    }

    @Test
    public void keepPhoneTheSameWhenNotSuitablePhoneAndRemovingNine() {
        String phoneWithoutNine = PhoneNumberFormatter.removeNine("+551188888888");

        assertEquals("+551188888888", phoneWithoutNine);
    }
}
