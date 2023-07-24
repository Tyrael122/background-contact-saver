import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../Utils/log_file_watcher.dart';

class StatusLoggerViewer extends StatefulWidget {
  const StatusLoggerViewer({super.key});

  @override
  State<StatusLoggerViewer> createState() => _StatusLoggerViewerState();
}

class _StatusLoggerViewerState extends State<StatusLoggerViewer> {
  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<LogFileMonitor>(
      create: (context) {
        final logFileWatcher = LogFileMonitor();
        logFileWatcher.startLogFileMonitoring();
        return logFileWatcher;
      },
      child: Consumer<LogFileMonitor>(
        builder: (context, logFileWatcher, _) {
          return Container(
            height: 200,
            width: 299,
            decoration: BoxDecoration(
                color: const Color(0xFFD9D9D9),
                borderRadius: BorderRadius.circular(5)),
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: ListView.separated(
                itemCount: logFileWatcher.logEntries.length,
                itemBuilder: (context, index) {
                  return Text(logFileWatcher.logEntries[index]);
                },
                separatorBuilder: (BuildContext context, int index) {
                  return const SizedBox(height: 5);
                },
              ),
            ),
          );
        },
      ),
    );
  }
}
