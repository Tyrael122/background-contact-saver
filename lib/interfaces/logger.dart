abstract interface class Logger {
  void logInfo(String message);
  void logError(String message);
  Stream<String> get logStream;
}
