package br.makesoftware.contacts_manager.contacts;

import androidx.annotation.NonNull;

public class PhoneNumberFormatter {

    private static final String REGEX_SOMENTE_NUMEROS = "[^0-9]";

    private PhoneNumberFormatter() {
    }

    public static String formatPhone(String phoneNumber) {
        phoneNumber = removeNonDigits(phoneNumber);

        phoneNumber = addCountryCode(phoneNumber);

        phoneNumber = removeSpaces(phoneNumber);

        return phoneNumber;
    }

    public static String removeNonDigits(String phoneNumber) {
        return phoneNumber.replaceAll(REGEX_SOMENTE_NUMEROS, "");
    }

    @NonNull
    public static String removeSpaces(String contactPhone) {
        return contactPhone.replaceAll(" ", "");
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

    public static String removeNine(String phoneNumber) {
        if (!phoneNumber.startsWith("+55") || !(phoneNumber.length() == 14)) return phoneNumber;
        return phoneNumber.substring(0, 5) + phoneNumber.substring(6);
    }
}
