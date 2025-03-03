import 'dart:async';

import 'package:contacts_manager/Utils/status_logger.dart';
import 'package:contacts_manager/constants/android_plataform_constants.dart';
import 'package:contacts_manager/utils/permission_manager.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../interfaces/logger.dart';

class AndroidContactServiceManager {
  final Logger statusLogger = StatusLogger();

  static const String _contactServiceKey = "fetchContactsFromApi";
  static const String _urlKey = "url";

  static const String _requestIntervalInMinKey = "requestIntervalInMin";

  final _channel = AndroidPlataformConstants.methodChannel;

  void setInterval(int requestIntervalInMin) async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setInt(_requestIntervalInMinKey, requestIntervalInMin);
  }

  void setUrl(String url) async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setString(_urlKey, url);
  }

  Future<bool> startService() async {
    bool isPermissionGranted = await PermissionManager.requestPermission(Permission.contacts);

    if (!isPermissionGranted) {
      statusLogger.logError("O serviço não possui as permissões necessárias para ser executado.");
      return false;
    }

    try {
      bool hasStartedSucessfully = await _callAndroidMethodToStartService();
      if (hasStartedSucessfully) {
        _setServiceAsOn();
        statusLogger.logInfo("O serviço foi iniciado com sucesso");
        return true;
      }
    } catch (e) {
      statusLogger.logError("Ocorreu um erro ao iniciar o serviço: $e");
    }

    statusLogger.logError("Ocorreu um erro ao iniciar o serviço.");
    return false;
  }

  Future<bool> stopService() async {
    try {
      bool hasStoppedSucessfully = await _callAndroidMethodToStopService();
      if (hasStoppedSucessfully) {
        _setServiceAsOff();
        statusLogger.logInfo("O serviço foi parado com sucesso.");
        return true;
      }
    } catch (e) {
      statusLogger.logError("Ocorreu um erro ao parar o serviço: $e");
    }

    statusLogger.logError("Ocorreu um erro ao parar o serviço.");
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

  Future<String> getUrl() async {
    final preferences = await SharedPreferences.getInstance();
    String? url = preferences.getString(_urlKey);

    if (url != null) {
      return url;
    } else {
      throw Exception("Url não definida");
    }
  }

  Future<bool> isServiceRunning() async {
    final preferences = await SharedPreferences.getInstance();
    return preferences.containsKey(_contactServiceKey);
  }

  Future<bool> _callAndroidMethodToStartService() async {
    return await _channel.invokeMethod(
        "startService", {"requestInterval": await getRequestInterval(), "url": await getUrl()});
  }

  Future<bool> _callAndroidMethodToStopService() async {
    return await _channel.invokeMethod("stopService");
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
