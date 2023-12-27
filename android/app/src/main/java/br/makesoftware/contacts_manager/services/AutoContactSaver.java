package br.makesoftware.contacts_manager.services;

import android.content.OperationApplicationException;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import br.makesoftware.contacts_manager.constants.LogType;
import br.makesoftware.contacts_manager.contacts.PhoneNumberFormatter;
import br.makesoftware.contacts_manager.interfaces.ContactRepository;
import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.Logger;
import br.makesoftware.contacts_manager.util.DateUtil;

public class AutoContactSaver {
    private final ConcernedPeopleNotifier concernedPeopleNotifier;
    private final ContactApiAdapter contactApiAdapter;
    private final ContactRepository contactRepository;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public AutoContactSaver(ContactApiAdapter contactApiAdapter, ContactRepository contactRepository, ConcernedPeopleNotifier concernedPeopleNotifier) {
        this.concernedPeopleNotifier = concernedPeopleNotifier;
        this.contactApiAdapter = contactApiAdapter;
        this.contactRepository = contactRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void savePendingContacts() {
        Logger.logInfo("Iniciando serviço às " + DateUtil.formateDate(LocalDateTime.now()) + ".", LogType.STATUS);

        List<String> contactsNotSent = tryFetchContactsNotSent();
        if (contactsNotSent == null) return;

        String executedMessage = "O serviço foi executado.";
        if (contactsNotSent.isEmpty()) {
            concernedPeopleNotifier.sendInfoMessage(executedMessage + " A requisição não trouxe nenhum contato.");
            return;
        }

        Logger.logInfo("A requisição trouxe " + contactsNotSent.size() + " contato(s).", LogType.STATUS);
        saveContacts(contactsNotSent);

        concernedPeopleNotifier.sendInfoMessage(executedMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> tryFetchContactsNotSent() {
        Logger.logInfo("Tentando fazer uma requisição para a API.", LogType.STATUS);

        List<String> contactsNotSent;
        try {
            contactsNotSent = contactApiAdapter.requestContactsNotSent();
        } catch (Exception e) {
            String errorMessage = "Ocorreu um erro ao fazer uma requisição para a API: " + e + ": " + e.getMessage();
            concernedPeopleNotifier.sendErrorMessage(errorMessage);

            return null;
        }

        Logger.logInfo("Foi feita uma requisição para a API.", LogType.STATUS);

        return contactsNotSent;
    }

    private void saveContacts(List<String> contactPhones) {
        for (String contactDisplayPhone : contactPhones) {

            contactDisplayPhone = PhoneNumberFormatter.removeNonDigits(contactDisplayPhone);
            if (isContactAlreadySavedInPhone(contactDisplayPhone)) continue;

            String formattedPhone = PhoneNumberFormatter.formatPhone(contactDisplayPhone);
            String formattedPhoneWithoutNine = PhoneNumberFormatter.removeNine(formattedPhone);
            trySaveContact(contactDisplayPhone, formattedPhone, formattedPhoneWithoutNine);
        }
    }

    private boolean isContactAlreadySavedInPhone(String contactDisplayPhone) {
        if (contactRepository.isContactSavedInPhone(contactDisplayPhone)) {
            Logger.logInfo("O contato '" + contactDisplayPhone + "' já está salvo no celular.", LogType.CONTACT);

            return true;
        }

        return false;
    }

    private void trySaveContact(String formattedDisplayPhone, String... formattedContactPhone) {
        try {
            contactRepository.saveContact(formattedDisplayPhone, formattedContactPhone);
            Logger.logInfo("Contato '" + formattedDisplayPhone + "' salvo com sucesso com os números '" + Arrays.toString(formattedContactPhone) + "'.", LogType.CONTACT);

        } catch (RemoteException | OperationApplicationException e) {
            Logger.logError("Ocorreu um erro ao salvar o contato '" + formattedContactPhone + "': " + e + ": " + e.getMessage(), LogType.CONTACT);
        }
    }
}
