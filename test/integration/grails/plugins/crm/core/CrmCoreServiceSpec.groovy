package grails.plugins.crm.core

/**
 * Test CrmCoreService methods.
 */
class CrmCoreServiceSpec extends grails.plugin.spock.IntegrationSpec {

    def crmCoreService

    def "test isDomainReference method"() {
        expect:
        crmCoreService.isDomainReference("testLookupEntity@42")
        !crmCoreService.isDomainReference("testLookupEntity/42")
        !crmCoreService.isDomainReference("fooEntity@42")
    }

    def "test isDomainClass method"() {
        expect:
        crmCoreService.isDomainClass(new TestLookupEntity(name: "test"))
        !crmCoreService.isDomainClass("I'm not a domain class, I'm a String!")
    }

    def "test getDomainClass method"() {
        expect:
        crmCoreService.getDomainClass("testLookupEntity") == TestLookupEntity

        when:
        crmCoreService.getDomainClass("foo")

        then:
        def e = thrown(ClassNotFoundException)
        e.message == 'foo'
    }

    def "test getReferenceIdentifier method"() {
        when:
        def m = new TestLookupEntity(name:"Test").save(flush: true)
        def identifier = crmCoreService.getReferenceIdentifier(m)

        then:
        m.id != null
        identifier == 'testLookupEntity@' + m.id
        crmCoreService.getReferenceIdentifier(identifier) == identifier
        crmCoreService.getReferenceIdentifier(null) == null
        crmCoreService.getReference(identifier) == m
        crmCoreService.getReference(null) == null
    }
}
