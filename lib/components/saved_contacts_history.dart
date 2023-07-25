import 'package:contacts_manager/constants/log_constants.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../Utils/log_file_watcher.dart';

class SavedContactsHistory extends StatelessWidget {
  const SavedContactsHistory({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<LogFileMonitor>(
      create: (context) {
        final logFileWatcher = LogFileMonitor();
        logFileWatcher.startLogFileMonitoring(LogConstants.contactLogPath);
        return logFileWatcher;
      },
      child: Consumer<LogFileMonitor>(
        builder: (context, logFileWatcher, _) {
          return ListView.separated(
            padding: const EdgeInsets.only(left: 33, right: 28, bottom: 8),
            addAutomaticKeepAlives: false,
            itemCount: logFileWatcher.logEntries.length,
            separatorBuilder: (BuildContext context, int index) =>
                const SizedBox(
              height: 10,
            ),
            itemBuilder: (BuildContext context, int index) {
              return Container(
                decoration: BoxDecoration(
                  color: const Color(0xFFD9D9D9),
                  borderRadius: BorderRadius.circular(5),
                ),
                child: ListTile(
                  title: Text(logFileWatcher.logEntries[index].message),
                  trailing: Text(logFileWatcher.logEntries[index].timestamp),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(5),
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
