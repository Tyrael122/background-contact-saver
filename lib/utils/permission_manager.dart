import 'package:permission_handler/permission_handler.dart';

class PermissionManager {
  static Future<bool> requestPermission(Permission permission) async {
    if (!await permission.isGranted) {
      var permissionStatus = await permission.request();

      if (permissionStatus.isPermanentlyDenied || permissionStatus.isDenied) {
        return false;
      }
    }

    return true;
  }
}