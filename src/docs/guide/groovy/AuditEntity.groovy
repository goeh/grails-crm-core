import grails.plugins.crm.core.AuditEntity

@AuditEntity
class Author {
  String name

  String toString() {
    "Author $name last updated ${lastUpdated ?: dateCreated}"
  }
}