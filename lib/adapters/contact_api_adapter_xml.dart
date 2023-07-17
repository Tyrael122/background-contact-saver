import '../interfaces/contact_api_adapter.dart';

class ContactAPIAdapterXML implements ContactAPIAdapter {
  @override
  List<String> requestContactsNotSent() {
    return ['99999999'];
  }
}