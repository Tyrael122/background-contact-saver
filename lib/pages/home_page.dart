import 'package:contacts_manager/components/saved_contacts_history.dart';
import 'package:contacts_manager/components/status_logger_viewer.dart';
import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {

  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> with AutomaticKeepAliveClientMixin<HomePage> {
  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);

    return const Center(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          SizedBox(height: 9),
          Padding(
            padding: EdgeInsets.only(left: 33, bottom: 3),
            child: Text('Status', style: TextStyle(fontSize: 20)),
          ),
          Padding(
            padding: EdgeInsets.only(left: 33, right: 28),
            child: StatusLoggerViewer(),
          ),
          SizedBox(height: 51),
          Padding(
            padding: EdgeInsets.only(left: 33, bottom: 3),
            child: Text('Contatos', style: TextStyle(fontSize: 20)),
          ),
          Expanded(
            child: SavedContactsHistory(),
          )
        ],
      ),
    );
  }
}
