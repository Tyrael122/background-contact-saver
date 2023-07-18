import 'package:flutter_contacts/flutter_contacts.dart';

class ContactManager {
  static List<Contact> contacts = List.empty(growable: true);

  bool isContactSavedInPhone(String contactPhone) {
    contacts = [Contact(phones: [Phone("9999999")])];

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
