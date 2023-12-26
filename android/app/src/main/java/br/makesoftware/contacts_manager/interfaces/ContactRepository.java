package br.makesoftware.contacts_manager.interfaces;

import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.util.List;

public interface ContactRepository {
    boolean isContactSavedInPhone(String contactDisplayPhone);
    List<String> fetchAllContactDisplayNames();
    void saveContact(String contactDisplayName, String contactPhoneNumber) throws RemoteException, OperationApplicationException;
}
