import 'dart:async';

import 'package:contacts_manager/interfaces/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';

class StatusLogger implements Logger {
  StreamController<String> _logController = StreamController<String>.broadcast();

  static const String _statusLogKey = "statusLog";
  static const int _maxLogAmount = 10;

  @override
  void logError(String message) async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    enforceLoggingConstraints(preferences);

    _logController.add(message);
    preferences.setStringList(_statusLogKey, await _logController.stream.toList());

    print(message);
  }

  @override
  void logInfo(String message) async {
    SharedPreferences preferences = await SharedPreferences.getInstance();
    enforceLoggingConstraints(preferences);
    message = formatMessage(message);

    _logController.add(message);
    preferences.setStringList(_statusLogKey, await _logController.stream.toList());

    print(message);
  }

  @override
  Stream<String> get logStream => _logController.stream;

  void enforceLoggingConstraints(SharedPreferences preferences) async {
    if (await _logController.stream.isEmpty) {
      addPreviousLogsToStream(preferences);
    }

    if (await _logController.stream.length >= _maxLogAmount) {
      deleteEarliestLog();
    }
  }

  void addPreviousLogsToStream(SharedPreferences preferences) {
    List<String>? logMessages = preferences.getStringList(_statusLogKey);
    for (String log in logMessages!) {
      _logController.add(log);
    }
  }

  void deleteEarliestLog() async {
    final tempList = await _logController.stream.toList();

    _logController = StreamController<String>.broadcast();

    for (var element in tempList) { _logController.add(element); }
  }

  String formatMessage(String message) {
    return "${getNowDateTimeWithoutMiliseconds()} - '$message'";
  }

  String getNowDateTimeWithoutMiliseconds() {
    DateTime now = DateTime.now();
    return "${now.day}-${now.month}-${now.year} ${now.hour}:${now.minute}:${now.second}";
  }
}
