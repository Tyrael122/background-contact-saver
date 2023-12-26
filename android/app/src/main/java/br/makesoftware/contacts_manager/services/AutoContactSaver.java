package br.makesoftware.contacts_manager.services;

import android.content.OperationApplicationException;
import android.os.Build;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.List;

import br.makesoftware.contacts_manager.constants.LogType;
import br.makesoftware.contacts_manager.contacts.ContactFormatter;
import br.makesoftware.contacts_manager.interfaces.ContactRepository;
import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.FileLogger;
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
        FileLogger.logInfo("Iniciando processamento às " + DateUtil.formateDate(LocalDateTime.now()) + ".", LogType.STATUS);

        List<String> contactsNotSent = tryFetchContactsNotSent();
        if (contactsNotSent == null) return;

        String infoMessage;
        if (contactsNotSent.isEmpty()) {
            infoMessage = "A requisição não trouxe nenhum contato.";

        } else {
            infoMessage = "A requisição trouxe " + contactsNotSent.size() + " contato(s).";
            saveContacts(contactsNotSent);
        }

        concernedPeopleNotifier.sendInfoMessage(infoMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> tryFetchContactsNotSent() {
        FileLogger.logInfo("Tentando fazer uma requisição para a API.", LogType.STATUS);

        List<String> contactsNotSent;
        try {
            contactsNotSent = contactApiAdapter.requestContactsNotSent();
        } catch (Exception e) {
            String errorMessage = "Ocorreu um erro ao fazer uma requisição para a API: " + e + ": " + e.getMessage();
            concernedPeopleNotifier.sendErrorMessage(errorMessage);

            return null;
        }

        FileLogger.logInfo("Foi feita uma requisição para a API.", LogType.STATUS);

        return contactsNotSent;
    }

    private void saveContacts(List<String> contactPhones) {
        for (String contactDisplayPhone : contactPhones) {

            contactDisplayPhone = ContactFormatter.removeSpaces(contactDisplayPhone);
            if (isContactAlreadySavedInPhone(contactDisplayPhone)) continue;

            String formattedContactPhone = formatContactPhone(contactDisplayPhone);
            trySaveContact(contactDisplayPhone, formattedContactPhone);
        }
    }

    @NonNull
    private static String formatContactPhone(String contactPhone) {
        String formattedContactPhone = ContactFormatter.formatContactPhone(contactPhone);

        FileLogger.logInfo("O contato '" + contactPhone + "' foi formatado para '" + formattedContactPhone + "'.", LogType.CONTACT);

        return formattedContactPhone;
    }

    private boolean isContactAlreadySavedInPhone(String contactDisplayPhone) {
        if (contactRepository.isContactSavedInPhone(contactDisplayPhone)) {
            FileLogger.logInfo("O contato '" + contactDisplayPhone + "' já está salvo no celular.", LogType.CONTACT);

            return true;
        }

        return false;
    }

    private void trySaveContact(String formattedDisplayPhone, String formattedContactPhone) {
        try {
            contactRepository.saveContact(formattedDisplayPhone, formattedContactPhone);
            FileLogger.logInfo("Contato '" + formattedDisplayPhone + "' salvo com sucesso com o número '" + formattedContactPhone + "'.", LogType.CONTACT);

        } catch (RemoteException | OperationApplicationException e) {
            FileLogger.logError("Ocorreu um erro ao salvar o contato '" + formattedContactPhone + "': " + e + ": " + e.getMessage(), LogType.CONTACT);
        }
    }
}
