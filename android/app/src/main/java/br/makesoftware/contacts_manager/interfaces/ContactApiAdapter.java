package br.makesoftware.contacts_manager.interfaces;

import java.util.List;

public interface ContactApiAdapter {
    List<String> requestContactsNotSent() throws Exception;
}
