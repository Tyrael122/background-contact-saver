import 'dart:async';

import 'package:contacts_manager/Utils/saved_contacts_logger.dart';
import 'package:contacts_manager/Utils/status_logger.dart';
import 'package:contacts_manager/controllers/contact_manager.dart';
import 'package:flutter_background/flutter_background.dart';

import '../interfaces/contact_api_adapter.dart';

class ContactManagerService {
  int _requestIntervalInMin = 5;

  final StatusLogger _statusLogger = StatusLogger();
  final SavedContactsLogger _savedContactsLogger = SavedContactsLogger();

  final ContactManager _contactManager = ContactManager();
  final ContactAPIAdapter _contactAPIAdapter;

  late Timer backgroundThread;

  ContactManagerService(this._contactAPIAdapter);

  void setInterval(int requestIntervalInMin) {
    _requestIntervalInMin = requestIntervalInMin;

    // stopService();
    // startService();
  }

  int getRequestInterval() {
    return _requestIntervalInMin;
  }

  Future<bool> startService() async {
    bool hasPermissions = await FlutterBackground.initialize(
        androidConfig: _getBackgroundConfig());
    if (!hasPermissions) {
      _statusLogger.logError(
          "As permissões para rodar o serviço em segundo plano não foram concedidas.");
      return false;
    }

    bool hasEnabledBackgroundExecution =
        await FlutterBackground.enableBackgroundExecution();

    if (!hasEnabledBackgroundExecution) {
      _statusLogger.logError("Ocorreu um erro ao inicializar o serviço.");
      return false;
    }

    backgroundThread = _startServiceThread();

    _statusLogger.logInfo("O serviço foi inicializado com sucesso.");
    return true;
  }

  Future<bool> stopService() async {
    backgroundThread.cancel();

    if (FlutterBackground.isBackgroundExecutionEnabled) {
      bool hasServiceStoppedSucessfully =
          await FlutterBackground.disableBackgroundExecution();
      if (hasServiceStoppedSucessfully) {
        _statusLogger.logInfo("O serviço foi parado com sucesso.");
      } else {
        _statusLogger.logError("Ocorreu um erro ao parar o serviço.");
      }
      return hasServiceStoppedSucessfully;
    }

    _statusLogger.logInfo("O serviço não está ligado.");
    return true;
  }

  FlutterBackgroundAndroidConfig _getBackgroundConfig() {
    return const FlutterBackgroundAndroidConfig(
      notificationTitle: "flutter_background example app",
      notificationText:
          "Background notification for keeping the example app running in the background",
      notificationImportance: AndroidNotificationImportance.Default,
      notificationIcon:
          AndroidResource(name: 'background_icon', defType: 'drawable'),
      showBadge: true,
    );
  }

  void saveContactsNonExistent() {
    List<String> contactsNotSent = _contactAPIAdapter.requestContactsNotSent();
    if (contactsNotSent.isEmpty) {
      _statusLogger.logInfo("A requisição não trouxe nenhum contato.");
      return;
    }

    _saveContacts(contactsNotSent);
  }

  void _saveContacts(List<String> contactsNotSent) {
    for (String contact in contactsNotSent) {
      if (_contactManager.isContactAlreadySavedInPhone(contact)) {
        _statusLogger.logInfo("Contato '$contact' já está salvo no celular.");
        continue;
      }

      bool hasContactBeenSaved = _contactManager.saveContact(contact);
      if (hasContactBeenSaved) {
        _savedContactsLogger.logInfo("Contato '$contact' salvo com sucesso.");
      } else {
        _savedContactsLogger
            .logError("Não foi possível salvar o contato '$contact'.");
      }
    }
  }

  Timer _startServiceThread() {
    return Timer.periodic(Duration(seconds: _requestIntervalInMin), (timer) {
      print("The background task is running.");
    });
  }
}
