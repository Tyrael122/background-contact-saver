import 'package:contacts_manager/interfaces/logger.dart';

class SavedContactsLogger implements Logger {
  @override
  void logError(String message) {
    print(message);
  }

  @override
  void logInfo(String message) {
    print(message);
  }
}