import 'package:flutter_contacts/flutter_contacts.dart';

class ContactManager {
  Future<bool> isContactSavedInPhone(String contactPhone) async {
    List<Contact> contacts = await FlutterContacts.getContacts();
    for (Contact contact in contacts) {
      for (Phone phone in contact.phones) {
        if (phone.number == contactPhone) return true;
      }
    }

    return false;
  }

  Future<bool> saveContact(String contactPhone) async {
    Contact(phones: [Phone(contactPhone)], displayName: contactPhone).insert();

    return await isContactSavedInPhone(contactPhone);
  }
}
