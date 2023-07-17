import 'package:contacts_service/contacts_service.dart';

class ContactManager {
  Future<bool> isContactSavedInPhone(String contact) async {
    List<Contact> matchedContacts = await ContactsService.getContactsForPhone(contact);

    return matchedContacts.isNotEmpty;
  }

  Future<bool> saveContact(String contactPhone) async {
    Contact newContact = Contact(givenName: contactPhone, phones: [Item(label: 'mobile', value: contactPhone)]);
    ContactsService.addContact(newContact);

    return await isContactSavedInPhone(contactPhone);
  }
}
