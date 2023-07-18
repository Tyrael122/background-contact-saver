import 'dart:async';

import 'package:contacts_manager/Utils/saved_contacts_logger.dart';
import 'package:contacts_manager/Utils/status_logger.dart';
import 'package:contacts_manager/controllers/contact_manager.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:workmanager/workmanager.dart';

import '../interfaces/contact_api_adapter.dart';
import '../interfaces/logger.dart';
import '../main.dart';

class ContactManagerService {
  final Logger statusLogger = StatusLogger();
  final Logger savedContactsLogger = SavedContactsLogger();

  final ContactManager _contactManager = ContactManager();
  final ContactAPIAdapter _contactAPIAdapter;

  static const String _fetchApiTaskKey = "fetchContactsFromApi";
  static const String _requestIntervalInMinKey = "requestIntervalInMin";

  ContactManagerService(this._contactAPIAdapter);

  void setInterval(int requestIntervalInMin) async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setInt(_requestIntervalInMinKey, requestIntervalInMin);

    stopService();
    startService();
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

  Future<bool> startService() async {
    try {
      Workmanager().initialize(callbackDispatcher, isInDebugMode: true);

      _scheduleServiceTask();

      _setServiceAsOn();

      statusLogger.logInfo("O serviço foi inicializado com sucesso. Service hashcode: $hashCode");
      return true;
    } on Exception {
      statusLogger.logError("Ocorreu um erro ao inicializar o serviço.");
      return false;
    }
  }

  Future<bool> stopService() async {
    try {
      Workmanager().cancelAll();

      _setServiceAsOff();

      statusLogger.logInfo("O serviço foi parado com sucesso.");
      return true;
    } on Exception {
      statusLogger.logError("Ocorreu um erro ao parar o serviço.");
      return false;
    }
  }

  void _saveNonExistentContacts() {
    List<String> contactsNotSent = _contactAPIAdapter.requestContactsNotSent();
    statusLogger.logInfo("Foi feita uma requisição para a API.");

    if (contactsNotSent.isEmpty) {
      statusLogger.logInfo("A requisição não trouxe nenhum contato.");
      return;
    }

    statusLogger
        .logInfo("A requisição trouxe ${contactsNotSent.length} contatos");
    _saveContacts(contactsNotSent);
  }

  Future<void> _saveContacts(List<String> contactsNotSent) async {
    for (String contact in contactsNotSent) {
      if (await _contactManager.isContactSavedInPhone(contact)) {
        statusLogger.logInfo("Contato '$contact' já está salvo no celular.");
        continue;
      }

      bool hasContactBeenSaved = await _contactManager.saveContact(contact);
      if (hasContactBeenSaved) {
        savedContactsLogger.logInfo("Contato '$contact' salvo com sucesso.");
      } else {
        savedContactsLogger
            .logError("Não foi possível salvar o contato '$contact'.");
      }
    }
  }

  Future<void> _scheduleServiceTask() {
    return Workmanager()
        .registerOneOffTask(_fetchApiTaskKey, _fetchApiTaskKey, initialDelay: const Duration(seconds: 5));
  }

  Future<bool> isServiceRunning() async {
    final preferences = await SharedPreferences.getInstance();
    return preferences.containsKey(_fetchApiTaskKey);
  }

  void _setServiceAsOn() async {
    final preferences = await SharedPreferences.getInstance();
    preferences.setBool(_fetchApiTaskKey, true);
  }

  void _setServiceAsOff() async {
    final preferences = await SharedPreferences.getInstance();
    preferences.remove(_fetchApiTaskKey);
  }

  Future<bool> taskExecutionManager(
      String task, Map<String, dynamic>? inputData) {
    switch (task) {
      case _fetchApiTaskKey:
        _saveNonExistentContacts();
        break;
    }
    return Future.value(true);
  }
}

@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    return MyHomePage.contactManagerService.taskExecutionManager(task, inputData);
  });
}
