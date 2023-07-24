import 'package:contacts_manager/interfaces/logger.dart';

import '../constants/android_plataform_constants.dart';

class StatusLogger implements Logger {
  final _channel = AndroidPlataformConstants.methodChannel;

  @override
  void logError(String message) async {
    _channel.invokeMethod("logError", {"message": message});
  }

  @override
  void logInfo(String message) async {
    _channel.invokeMethod("logInfo", {"message": message});
  }
}
