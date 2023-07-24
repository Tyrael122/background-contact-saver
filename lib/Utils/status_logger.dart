import 'dart:io';

import 'package:contacts_manager/constants/log_constants.dart';
import 'package:contacts_manager/interfaces/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';

class StatusLogger implements Logger {
  static const int _maxLogAmount = 10;

  int logsCounter = 0;

  @override
  void logError(String message) async {
    message = formatMessage(message);
    // enforceLoggingConstraints();

    var logFile = File(await LogConstants.logFilePath);

    try {
      logFile.createSync(recursive: true);
      logFile.writeAsStringSync('$message\n', mode: FileMode.append);
    } catch (e) {
    }



    logsCounter++;
  }

  @override
  void logInfo(String message) async {
    message = formatMessage(message);
    print("A mensagem de log foi formatada.");
    // enforceLoggingConstraints();

    var logFile = File(await LogConstants.logFilePath);
    print("Obtemos o arquivo de log em uma variável.");

    try {
      logFile.createSync(recursive: true);
      logFile.writeAsStringSync('$message\n', mode: FileMode.append);
    } catch (e) {
      print("Não foi possível escrever no arquivo: $e");
    }

    logsCounter++;
  }

  void enforceLoggingConstraints(SharedPreferences preferences) async {
    // if (await _logController.stream.isEmpty) {
    //   addPreviousLogsToStream(preferences);
    // }

    // if (await _logController.stream.length >= _maxLogAmount) {
    //   deleteEarliestLog();
    // }
  }

  // void addPreviousLogsToStream(SharedPreferences preferences) {
  //   List<String>? logMessages = preferences.getStringList(_statusLogKey);
  //   for (String log in logMessages!) {
  //     _logController.add(log);
  //     logsCounter++;
  //   }
  // }
  //
  // void deleteEarliestLog() async {
  //   final tempList = await _logController.stream.toList();
  //
  //   _logController = StreamController<String>.broadcast();
  //
  //   for (var element in tempList) {
  //     _logController.add(element);
  //   }
  // }

  String formatMessage(String message) {
    return "${getNowDateTimeWithoutMiliseconds()} - '$message'";
  }

  String getNowDateTimeWithoutMiliseconds() {
    DateTime now = DateTime.now();
    return "${now.day}-${now.month}-${now.year} ${now.hour}:${now.minute}:${now.second}";
  }
}
