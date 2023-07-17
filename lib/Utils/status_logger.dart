import 'package:contacts_manager/interfaces/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';

class StatusLogger implements Logger {
  static final List<String> logs = List.empty(growable: true);
  @override
  void logError(String message) {

    final preferences = SharedPreferences.getInstance();


    print(message);
  }

  @override
  void logInfo(String message) {
    print(message);
  }
}
