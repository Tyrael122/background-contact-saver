package br.makesoftware.contacts_manager.services;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.List;

import br.makesoftware.contacts_manager.adapters.XmlApiAdapter;
import br.makesoftware.contacts_manager.constants.LogType;
import br.makesoftware.contacts_manager.interfaces.ApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.FileLogger;
import br.makesoftware.contacts_manager.util.ContactManager;
import br.makesoftware.contacts_manager.util.DateUtil;

public class AutoContactSaver {
    private final ConcernedPeopleNotifier concernedPeopleNotifier;
    private final ApiAdapter apiAdapter;
    private final ContactManager contactManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public AutoContactSaver(Context context) {

        concernedPeopleNotifier = new ConcernedPeopleNotifier(context);
        apiAdapter = new XmlApiAdapter();

        contactManager = new ContactManager(context.getContentResolver());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void savePendingContacts() {
        FileLogger.logInfo("Iniciando processamento às " + DateUtil.formateDate(LocalDateTime.now())  + ".", LogType.STATUS);

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
            contactsNotSent = apiAdapter.requestContactsNotSent();
        } catch (Exception e) {
            String errorMessage = "Ocorreu um erro ao fazer uma requisição para a API: " + e + ": " + e.getMessage();
            concernedPeopleNotifier.sendErrorMessage(errorMessage);

            return null;
        }

        FileLogger.logInfo("Foi feita uma requisição para a API.", LogType.STATUS);
        
        return contactsNotSent;
    }

    private void saveContacts(List<String> contactPhones) {
        for (String contactPhone : contactPhones) {
            String formattedContactPhone = formatContactPhone(contactPhone);

            if (isContactAlreadySavedInPhone(formattedContactPhone)) continue;

            trySaveContact(contactPhone, formattedContactPhone);
        }
    }

    @NonNull
    private static String formatContactPhone(String contactPhone) {
        String formattedContactPhone = ContactManager.formatContactPhone(contactPhone);

        FileLogger.logInfo("O contato '" + contactPhone + "' foi formatado para '" + formattedContactPhone + "'.", LogType.CONTACT);

        return formattedContactPhone;
    }

    private boolean isContactAlreadySavedInPhone(String formattedContactPhone) {
        if (contactManager.isContactSavedInPhone(formattedContactPhone)) {
            FileLogger.logInfo("O contato " + formattedContactPhone + " já está salvo no celular.", LogType.CONTACT);

            return true;
        }

        return false;
    }

    private void trySaveContact(String contactPhone, String formattedContactPhone) {
        try {
            contactManager.insertContact(contactPhone, formattedContactPhone);
            FileLogger.logInfo("Contato '" + contactPhone + "' salvo com sucesso com o número '" + formattedContactPhone + "'.", LogType.CONTACT);

        } catch (RemoteException | OperationApplicationException e) {
            FileLogger.logError("Ocorreu um erro ao salvar o contato '" + formattedContactPhone + "': " + e + ": " + e.getMessage(), LogType.CONTACT);
        }
    }
}
