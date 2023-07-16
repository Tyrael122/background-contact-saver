import 'package:contacts_manager/controllers/contact_manager_service.dart';
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
  bool _isServiceOn = false;
  final ContactManagerService _contactManagerService =
      ContactManagerService(ContactAPIAdapterXML());

  int _selectedIndex = 0;

  Widget getBodyWidget(int selectedIndex) {
    switch (selectedIndex) {
      case 0:
        return const HomePage();
      case 1:
        return SettingsPage(contactManagerService: _contactManagerService,);
      default:
        return const Placeholder();
    }
  }

  Text getTitle(int selectedIndex) {
    switch (selectedIndex) {
      case 1:
        return const Text("Configurações");
      default:
        return const Text("Gerenciador de contatos");
    }
  }

  final MaterialStateProperty<Color?> trackColor =
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
                setState(() {
                  _isServiceOn = value;
                });
                if (_isServiceOn) {
                  _contactManagerService.startService();
                } else {
                  _contactManagerService.stopService();
                }
              },
              trackColor: trackColor,
              value: _isServiceOn,
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
