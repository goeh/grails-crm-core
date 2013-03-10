package grails.plugins.crm.core

import spock.lang.Specification

/**
 * Test CrmLookupEntity abstract domain.
 */
class CrmLookupEntitySpec extends Specification {

    def "test comparable"() {
        expect:
        new TestLookupEntity(orderIndex: 1) == new TestLookupEntity(orderIndex: 1)
        new TestLookupEntity(orderIndex: 1) < new TestLookupEntity(orderIndex: 2)
        new TestLookupEntity(orderIndex: 2) > new TestLookupEntity(orderIndex: 1)
    }
}
