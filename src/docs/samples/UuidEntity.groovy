import grails.plugins.crm.core.UuidEntity

@UuidEntity
class MyEvent {

  String toString() {
    "[$guid]"
  }
}