import 'package:contacts_manager/components/status_logger_viewer.dart';
import 'package:contacts_manager/controllers/contact_manager_service.dart';
import 'package:contacts_manager/pages/add_contact_page.dart';
import 'package:contacts_manager/pages/home_page.dart';
import 'package:contacts_manager/pages/settings_page.dart';
import 'package:flutter/material.dart';

import 'adapters/contact_api_adapter_xml.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF386BF6)),
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _isServiceRunning = false;

  final ContactManagerService _contactManagerService =
      ContactManagerService(ContactAPIAdapterXML());

  int _selectedIndex = 0;

  @override
  void initState() {
    super.initState();
    _getServiceStatus();
  }

  Future<void> _getServiceStatus() async {
    bool isRunning = await _contactManagerService.isServiceRunning();
    setState(() {
      _isServiceRunning = isRunning;
    });
  }

  Widget getBodyWidget(int selectedIndex) {
    switch (selectedIndex) {
      case 0:
        return HomePage(
          contactManagerService: _contactManagerService,
        );
      case 1:
        return SettingsPage(
          contactManagerService: _contactManagerService,
        );
      case 2:
        return const AddContactPage();
      default:
        return const Placeholder();
    }
  }

  Text getTitle(int selectedIndex) {
    switch (selectedIndex) {
      case 1:
        return const Text("Configurações");
      case 2:
        return const Text("Cadastro de contato");
      default:
        return const Text("Gerenciador de contatos");
    }
  }

  void _toggleService(bool value) async {
    if (value) {
      _contactManagerService.startService().then((value) {
        setState(() {
          _isServiceRunning = value;
        });
      });
    } else {
      _contactManagerService.stopService().then((value) {
        setState(() {
          _isServiceRunning = !value;
        });
      });
    }
  }

  final MaterialStateProperty<Color?> _trackColor =
      MaterialStateProperty.resolveWith<Color?>(
    (Set<MaterialState> states) {
      if (states.contains(MaterialState.selected)) {
        return Colors.black;
      }
      return null;
    },
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).primaryColor,
        title: getTitle(_selectedIndex),
        titleTextStyle: const TextStyle(color: Colors.white, fontSize: 20),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 13),
            child: Switch(
              onChanged: (value) {
                _toggleService(value);
              },
              trackColor: _trackColor,
              value: _isServiceRunning,
            ),
          )
        ],
      ),
      body: getBodyWidget(_selectedIndex),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home_filled), label: "Home"),
          BottomNavigationBarItem(
              icon: Icon(Icons.settings), label: "Configurações"),
          BottomNavigationBarItem(
              icon: Icon(Icons.add), label: "Cadastrar contato"),
        ],
        onTap: (int index) {
          setState(() {
            _selectedIndex = index;
          });
        },
      ),
    );
  }
}
