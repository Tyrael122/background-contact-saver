import 'package:flutter/services.dart';

class AndroidPlataformConstants {
  static const MethodChannel channel =
      MethodChannel("br.makesoftware.contacts_manager/channel");

  static MethodChannel get methodChannel => channel;
}
