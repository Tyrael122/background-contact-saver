import 'package:path_provider/path_provider.dart';

class LogConstants {
  static const String _contactLoggerName = "ContactLogger";
  static const String _statusLoggerName = "StatusLogger";

  static Future<String> getLogPath() async {
    String filesDir = (await getApplicationDocumentsDirectory()).path;
    filesDir = filesDir.replaceAll("app_flutter", "files");

    return filesDir;
  }

  static Future<String> get statusLogPath async {
    return '${await getLogPath()}/logs/$_statusLoggerName.txt';
  }

  static Future<String> get contactLogPath async {
    return '${await getLogPath()}/logs/$_contactLoggerName.txt';
  }
}