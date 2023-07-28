import 'package:contacts_manager/components/service_switch.dart';
import 'package:contacts_manager/controllers/android_contact_service_manager.dart';
import 'package:contacts_manager/pages/home_page.dart';
import 'package:contacts_manager/pages/settings_page.dart';
import 'package:flutter/material.dart';

Future<void> main() async {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
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

  static final AndroidContactServiceManager contactManagerService =
      AndroidContactServiceManager();

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final PageController _pageController = PageController(initialPage: 0);
  int _selectedIndex = 0;
  final List<Widget> _pages = [
    const HomePage(),
    SettingsPage()
  ];

  Text getTitle(int selectedIndex) {
    switch (selectedIndex) {
      case 1:
        return const Text("Configurações");
      default:
        return const Text("Gerenciador de contatos");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).primaryColor,
        title: getTitle(_selectedIndex),
        titleTextStyle: const TextStyle(color: Colors.white, fontSize: 20),
        actions: const [
          Padding(
            padding: EdgeInsets.only(right: 13),
            child: ServiceSwitch(),
          )
        ],
      ),
      body: PageView(
        controller: _pageController,
        physics: const NeverScrollableScrollPhysics(),
        children: _pages,
      ),
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

          _pageController.jumpToPage(index);
        },
      ),
    );
  }

  @override
  void dispose() {
    _pageController.dispose();

    super.dispose();
  }
}
