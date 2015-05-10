import grails.plugins.crm.core.UuidEntity

@UuidEntity(index = true)
class MyEvent {

  String toString() {
    "[$guid]"
  }
}