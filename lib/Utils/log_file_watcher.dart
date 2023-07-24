import 'dart:async';
import 'dart:io';

import 'package:contacts_manager/constants/log_constants.dart';
import 'package:flutter/material.dart';

class LogFileMonitor extends ChangeNotifier {
  static const _refreshInterval = Duration(seconds: 2);
  List<String> logEntries = [];

  void startLogFileMonitoring() async {
    Timer.periodic(_refreshInterval, (_) async {
      final newLogEntries = await readLogFile();
      if (logEntries == newLogEntries) {
        return;
      }

      logEntries = newLogEntries;
      notifyListeners();
    });
  }

  Future<List<String>> readLogFile() async {
    final logFile = File(await LogConstants.logFilePath);

    if (await logFile.exists()) {
      final lines = await logFile.readAsLines();
      return List.from(lines.toList().reversed);
    } else {
      return [];
    }
  }
}
