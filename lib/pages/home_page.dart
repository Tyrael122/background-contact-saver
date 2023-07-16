import 'package:contacts_manager/components/saved_contacts_history.dart';
import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

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
            child: Container(
              height: 62,
              width: 299,
              decoration: BoxDecoration(
                  color: const Color(0xFFD9D9D9),
                  borderRadius: BorderRadius.circular(5)),
            ),
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
