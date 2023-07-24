import 'dart:async';

import 'package:contacts_manager/Utils/status_logger.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../interfaces/logger.dart';

class AndroidContactServiceManager {
  final Logger statusLogger = StatusLogger();

  static const String _contactServiceKey = "fetchContactsFromApi";
  static const String _requestIntervalInMinKey = "requestIntervalInMin";

  static const MethodChannel channel =
      MethodChannel("br.makesoftware.contacts_manager/channel");

  AndroidContactServiceManager();

  void setInterval(int requestIntervalInMin) async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setInt(_requestIntervalInMinKey, requestIntervalInMin);

    stopService();
    startService();
  }

  Future<bool> startService() async {
    try {
      bool hasStartedSucessfully = await _callAndroidMethodToStartService();
      if (hasStartedSucessfully) {
        _setServiceAsOn();
        return true;
      }
    } catch (e) {
      statusLogger.logError("Error starting the service: $e");
    }

    return false;
  }

  Future<bool> stopService() async {
    try {
      bool hasStoppedSucessfully = await _callAndroidMethodToStopService();
      if (hasStoppedSucessfully) {
        _setServiceAsOff();
        return true;
      }

      statusLogger.logInfo("O serviço foi parado com sucesso.");
    } on Exception {
      statusLogger.logError("Ocorreu um erro ao parar o serviço.");
    }

    return false;
  }

  Future<int> getRequestInterval() async {
    final preferences = await SharedPreferences.getInstance();
    int? requestIntervalInMin = preferences.getInt(_requestIntervalInMinKey);

    if (requestIntervalInMin != null) {
      return requestIntervalInMin;
    } else {
      setInterval(15);
      return getRequestInterval();
    }
  }

  Future<bool> isServiceRunning() async {
    final preferences = await SharedPreferences.getInstance();
    return preferences.containsKey(_contactServiceKey);
  }

  Future<bool> _callAndroidMethodToStartService() async {
    bool? hasStartedSucessfully =
        await channel.invokeMethod<bool>("startService");

    if (hasStartedSucessfully != null) {
      return hasStartedSucessfully == true;
    }

    return false;
  }

  Future<bool> _callAndroidMethodToStopService() async {
    bool? hasStoppedSucessfully =
        await channel.invokeMethod<bool>("stopService");

    if (hasStoppedSucessfully != null) {
      return hasStoppedSucessfully == true;
    }

    return false;
  }

  void _setServiceAsOn() async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setBool(_contactServiceKey, true);
  }

  void _setServiceAsOff() async {
    final preferences = await SharedPreferences.getInstance();
    preferences.remove(_contactServiceKey);
  }
}
