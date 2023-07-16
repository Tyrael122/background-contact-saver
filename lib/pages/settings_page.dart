import 'package:flutter/material.dart';

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

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
                  trailing: const SizedBox(
                    width: 50,
                    height: 35,
                    child: TextField(
                      textAlignVertical: TextAlignVertical.top,
                      keyboardType: TextInputType.number,
                      maxLength: 2,
                      maxLines: 1,
                      style: TextStyle(fontSize: 16, height: 1.2),
                      decoration: InputDecoration(
                        isDense: true,
                        counterText: '',
                        border: OutlineInputBorder(),
                        filled: true,
                        fillColor: Colors.white,
                      ),
                    ),
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
