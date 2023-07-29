package br.makesoftware.contacts_manager.adapters;

import java.util.List;

import br.makesoftware.contacts_manager.interfaces.ApiAdapter;

public class XmlApiAdapter implements ApiAdapter {
    @Override
    public List<String> requestContactsNotSent() {
        return List.of("333333333");
    }
}
