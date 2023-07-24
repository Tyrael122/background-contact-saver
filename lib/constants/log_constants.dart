import 'package:path_provider/path_provider.dart';

class LogConstants {
  static Future<String> get logFilePath async {
    final directory = await getApplicationDocumentsDirectory();
    return '${directory.path}/logs/log.txt';
  }
}