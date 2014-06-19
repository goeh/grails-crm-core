import grails.plugins.crm.core.TenantEntity

@TenantEntity
class Customer {
  String name

  String toString() {
    "#$tenantId $name"
  }
}
