import 'package:path_provider/path_provider.dart';

class LogConstants {
  static const String _contactLoggerFileName = "CONTACT";
  static const String _statusLoggerFilename = "STATUS";

  static Future<String> getLogPath() async {
    String filesDir = (await getApplicationDocumentsDirectory()).path;
    filesDir = filesDir.replaceAll("app_flutter", "files");

    return filesDir;
  }

  static Future<String> get statusLogPath async {
    return '${await getLogPath()}/logs/$_statusLoggerFilename.txt';
  }

  static Future<String> get contactLogPath async {
    return '${await getLogPath()}/logs/$_contactLoggerFileName.txt';
  }
}