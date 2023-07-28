package br.makesoftware.contacts_manager;

import java.util.List;

public class XmlApiAdapter implements ApiAdapter {
    @Override
    public List<String> requestContactsNotSent() {
        return List.of("333333333");
    }
}
