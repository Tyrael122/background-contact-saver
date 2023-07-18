import 'package:contacts_manager/controllers/contact_manager_service.dart';
import 'package:flutter/material.dart';

class StatusLoggerViewer extends StatefulWidget {
  const StatusLoggerViewer({super.key, required this.contactManagerService});

  final ContactManagerService contactManagerService;

  @override
  State<StatusLoggerViewer> createState() => _StatusLoggerViewerState();
}

class _StatusLoggerViewerState extends State<StatusLoggerViewer> {
  final List<String> _logEntries = List.empty(growable: true);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 200,
      width: 299,
      decoration: BoxDecoration(
          color: const Color(0xFFD9D9D9),
          borderRadius: BorderRadius.circular(5)),
      child: StreamBuilder(
          stream: widget.contactManagerService.statusLogger.logStream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              _logEntries.add(snapshot.data!);
            }
            return ListView.builder(
              reverse: true,
              itemCount: _logEntries.length,
              itemBuilder: (context, index) => Text(_logEntries[index]),
            );
          }),
    );
  }
}
