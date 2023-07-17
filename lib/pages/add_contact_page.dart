import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';

class AddContactPage extends StatefulWidget {
  const AddContactPage({super.key});

  @override
  State<StatefulWidget> createState() => _AddContactPageState();
}

class _AddContactPageState extends State<AddContactPage> {
  Contact contact = Contact();
  PostalAddress address = PostalAddress(label: "Home");
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Center(
        child: Column(
          children: [
            Expanded(
              child: Form(
                key: _formKey,
                child: ListView(
                  children: [
                    TextFormField(
                      decoration: const InputDecoration(labelText: 'First name'),
                      onSaved: (v) => contact.givenName = v,
                    ),
                    TextFormField(
                      decoration: const InputDecoration(labelText: 'Phone'),
                      onSaved: (v) =>
                      contact.phones = [Item(label: "mobile", value: v)],
                      keyboardType: TextInputType.phone,
                    ),
                  ],
                ),
              ),
            ),
            TextButton(
              onPressed: () {
                _formKey.currentState?.save();
                ContactsService.addContact(contact);
              },
              child: const Text("Salvar"),
            )
          ],
        ),
      );
  }
}