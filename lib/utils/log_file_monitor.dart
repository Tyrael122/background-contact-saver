import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';

class LogFileMonitor extends ChangeNotifier {
  static const _refreshInterval = Duration(seconds: 2);
  List<LogRecord> logEntries = [];
  late Timer _timer;

  void startLogFileMonitoring(Future<String> logFilePath) async {
    Function runnable = getLogFileMonitoringFunction(await logFilePath);
    runnable.call();

    _timer = Timer.periodic(_refreshInterval, (ignoredTimer) {
      runnable.call();
    });
  }

  Future<List<LogRecord>> readLogFile(String logFilePath) async {
    final logFile = File(logFilePath);

    List<String> tempLogEntries = [];

    if (await logFile.exists()) {
      final lines = await logFile.readAsLines();
      tempLogEntries.addAll(lines.toList().reversed);
    }

    List<LogRecord> newLogEntries = [];
    for (String logEntry in tempLogEntries) {
      newLogEntries.add(LogRecord(logEntry));
    }

    return newLogEntries;
  }

  Function getLogFileMonitoringFunction(String logFilePath) {
    return () async {
      final newLogEntries = await readLogFile(logFilePath);
      if (logEntries == newLogEntries) {
        return;
      }

      logEntries = newLogEntries;
      notifyListeners();
    };
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }
}

class LogRecord {
  String timestamp = "";
  String message = "";

  String separator = "-";

  LogRecord(String log) {
    int indexOfSeparator = log.indexOf(separator);
    if (indexOfSeparator == -1) return;

    timestamp = log.substring(0, indexOfSeparator).trim();
    message = log.substring(indexOfSeparator + 2).trim();
  }

  String get log {
    if (timestamp == "" && message == "") return "";
    return "$timestamp - $message";
  }
}
