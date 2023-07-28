import 'package:contacts_manager/controllers/android_contact_service_manager.dart';
import 'package:flutter/material.dart';

class SettingsPage extends StatelessWidget {
  SettingsPage({super.key, required this.contactManagerService});

  final AndroidContactServiceManager contactManagerService;
  final _intervalRequestApiController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          const SizedBox(height: 9),
          Expanded(
            child: ListView.separated(
              padding: const EdgeInsets.only(left: 33, right: 28, bottom: 8),
              addAutomaticKeepAlives: false,
              itemCount: 1,
              separatorBuilder: (BuildContext context, int index) =>
                  const SizedBox(
                height: 8,
              ),
              itemBuilder: (BuildContext context, int index) {
                return ListTile(
                  tileColor: const Color(0xFFD9D9D9),
                  title: const Text("Intervalo de requisições na API (min)"),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5)),
                  trailing: SizedBox(
                    width: 50,
                    height: 35,
                    child: FutureBuilder(
                      future: contactManagerService.getRequestInterval(),
                      builder: (BuildContext context,
                          AsyncSnapshot<dynamic> snapshot) {
                        _intervalRequestApiController.text = "15";

                        if (snapshot.hasData) {
                          _intervalRequestApiController.text = snapshot.data.toString();
                        }

                        return TextField(
                          controller: _intervalRequestApiController,
                          textAlignVertical: TextAlignVertical.top,
                          keyboardType: TextInputType.number,
                          maxLength: 2,
                          maxLines: 1,
                          style: const TextStyle(fontSize: 16, height: 1.2),
                          decoration: const InputDecoration(
                            isDense: true,
                            counterText: '',
                            border: OutlineInputBorder(),
                            filled: true,
                            fillColor: Colors.white,
                          ),
                        );
                      },
                    ),
                  ),
                );
              },
            ),
          ),
          ElevatedButton(
              style: ElevatedButton.styleFrom(
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(0)),
              ),
              onPressed: () {
                contactManagerService
                    .setInterval(int.parse(_intervalRequestApiController.text));
              },
              child: const Text("Salvar"))
        ],
      ),
    );
  }
}
