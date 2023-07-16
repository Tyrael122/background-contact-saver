import 'package:flutter/material.dart';

class SavedContactsHistory extends StatelessWidget {
  const SavedContactsHistory({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.only(left: 33, right: 28, bottom: 8),
      addAutomaticKeepAlives: false,
      itemCount: 30,
      separatorBuilder: (BuildContext context, int index) =>
      const SizedBox(
        height: 10,
      ),
      itemBuilder: (BuildContext context, int index) {
        return Container(
          decoration: BoxDecoration(
            color: const Color(0xFFD9D9D9),
            borderRadius: BorderRadius.circular(5),
          ),
          child: ListTile(
            title: const Text("999999999"),
            trailing: const Text("01/01/2003 - 12:59"),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(5),
            ),
          ),
        );
      },
    );
  }

}