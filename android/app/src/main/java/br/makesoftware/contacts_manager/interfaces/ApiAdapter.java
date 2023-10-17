package br.makesoftware.contacts_manager.interfaces;

import java.util.List;

public interface ApiAdapter {
    List<String> requestContactsNotSent() throws Exception;
}
