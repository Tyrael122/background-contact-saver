package br.makesoftware.contacts_manager.adapters;

import java.util.List;

import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;

public class MockApiAdapter implements ContactApiAdapter {

    @Override
    public List<String> requestContactsNotSent() {
        return List.of("12 9 8888-8888");
    }
}
