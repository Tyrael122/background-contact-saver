import 'package:contacts_manager/components/saved_contacts_history.dart';
import 'package:contacts_manager/components/status_logger_viewer.dart';
import 'package:contacts_manager/controllers/contact_manager_service.dart';
import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  final ContactManagerService contactManagerService;

  const HomePage({super.key, required this.contactManagerService});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          const SizedBox(height: 9),
          const Padding(
            padding: EdgeInsets.only(left: 33, bottom: 3),
            child: Text('Status', style: TextStyle(fontSize: 20)),
          ),
          Padding(
            padding: const EdgeInsets.only(left: 33, right: 28),
            child: StatusLoggerViewer(contactManagerService: contactManagerService),
          ),
          const SizedBox(height: 51),
          const Padding(
            padding: EdgeInsets.only(left: 33, bottom: 3),
            child: Text('Contatos', style: TextStyle(fontSize: 20)),
          ),
          const Expanded(
            child: SavedContactsHistory(),
          )
        ],
      ),
    );
  }
}
