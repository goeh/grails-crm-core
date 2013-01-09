package grails.plugins.crm.core

import spock.lang.Specification
import grails.plugins.crm.util.Graph

/**
 * Test dependency graph (grails.plugins.crm.util.Graph).
 */
class GraphSpec extends Specification {
    def "test plugin dependency graph"() {
        when:
        Graph g = new Graph()
        // add the vertices
        g.addVertex("crmContact")
        g.addVertex("crmApartment")
        g.addVertex("crmAgreement")
        g.addVertex("crmTask")
        g.addVertex("crmTags")
        g.addVertex("crmNotes")
        g.addVertex("crmInvitation")
        g.addVertex("crmContent")
        g.addVertex("crmProduct")

        // add edges to create linking structure
        g.addEdge("crmApartment", "crmContact")
        g.addEdge("crmAgreement", "crmContact")
        g.addEdge("crmAgreement", "crmTask") // fake
        g.addEdge("crmApartment", "crmTask") // fake
        g.addEdge("crmProduct", "crmContact")
        g.addEdge("crmTask", "crmContact") // fake

        then:
        g.getSources(g.getVertex("crmContact")).join(', ') == "crmApartment, crmAgreement, crmProduct, crmTask"
        g.getSources(g.getVertex("crmTask")).join(', ') == "crmAgreement, crmApartment"
        g.getTargets(g.getVertex("crmTask")).join(', ') == "crmContact"
        g.toString() == "crmApartment->crmContact, crmAgreement->crmContact, crmAgreement->crmTask, crmApartment->crmTask, crmProduct->crmContact, crmTask->crmContact, crmTags, crmNotes, crmInvitation, crmContent"

        when:
        def itor = g.iterator()
        def s = new StringBuilder()
        while(itor.hasNext()) {
            def v = itor.next()
            if(s.length()) {
                s << ", "
            }
            s << v.toString()
        }

        then:
        s.toString() == "crmAgreement, crmApartment, crmProduct, crmTask, crmContent, crmInvitation, crmNotes, crmTags, crmContact"
    }
}
