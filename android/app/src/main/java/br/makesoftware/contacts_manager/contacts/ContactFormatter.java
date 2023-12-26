package br.makesoftware.contacts_manager.contacts;

import androidx.annotation.NonNull;

public class ContactFormatter {

    private static final String REGEX_SOMENTE_NUMEROS = "[^0-9]";

    private ContactFormatter() {
    }

    public static String formatContactPhone(String contactPhone) {
        contactPhone = contactPhone.replaceAll(REGEX_SOMENTE_NUMEROS, "");

        contactPhone = addCountryCode(contactPhone);

        contactPhone = removeSpaces(contactPhone);

        return contactPhone;
    }

    @NonNull
    public static String removeSpaces(String contactPhone) {
        contactPhone = contactPhone.replaceAll(" ", "");
        return contactPhone;
    }

    @NonNull
    private static String addCountryCode(String contactPhone) {
        if (contactPhone.startsWith("55")) {
            contactPhone = "+" + contactPhone;
        } else {
            contactPhone = "+55" + contactPhone;
        }
        return contactPhone;
    }
}
