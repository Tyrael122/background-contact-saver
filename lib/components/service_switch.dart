import 'package:flutter/material.dart';

import '../main.dart';

class ServiceSwitch extends StatefulWidget {
  const ServiceSwitch({super.key});

  @override
  State<ServiceSwitch> createState() => _ServiceSwitchState();
}

class _ServiceSwitchState extends State<ServiceSwitch> {
  bool _isServiceRunning = false;

  @override
  void initState() {
    super.initState();
    _getServiceStatus();
  }

  Future<void> _getServiceStatus() async {
    bool isRunning = await MyHomePage.contactManagerService.isServiceRunning();
    setState(() {
      _isServiceRunning = isRunning;
    });
  }

  void _toggleService(bool value) async {
    if (value) {
      MyHomePage.contactManagerService.startService().then((value) {
        setState(() {
          _isServiceRunning = value;
        });
      });
    } else {
      MyHomePage.contactManagerService.stopService().then((value) {
        setState(() {
          _isServiceRunning = !value;
        });
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Switch(
      onChanged: (value) {
        _toggleService(value);
      },
      trackColor: _trackColor,
      value: _isServiceRunning,
    );
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
}