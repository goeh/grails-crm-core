package grails.plugins.crm.core

import grails.plugins.crm.util.Edge
import grails.plugins.crm.util.Vertex
import spock.lang.Specification
import grails.plugins.crm.util.Graph

/**
 * Test dependency graph (grails.plugins.crm.util.Graph).
 */
class GraphSpec extends Specification {

    def "test Vertex constructor"() {
        when:
        new Vertex(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "test Vertex#hashCode"() {
        expect:
        new Vertex("foo").hashCode() == new Vertex("foo").hashCode()
        new Vertex("foo").hashCode() != new Vertex("bar").hashCode()
    }

    def "test Vertex#equals"() {
        expect:
        new Vertex("foo") == new Vertex("foo")
        new Vertex("foo") != new Vertex("bar")
        new Vertex("foo") != "foo"
    }

    def "test Edge#equals"() {
        when:
        def s = new Vertex("source")
        def t = new Vertex("target")
        def e1 = new Edge(s, t)
        def e2 = new Edge(s, t)
        def e3 = new Edge(s, t, 0.0)
        def e4 = new Edge(t, s)

        then:
        e1 == e2
        e2 == e3
        e1 != e4
    }

    def "test Edge#hashCode"() {
        when:
        def s = new Vertex("source")
        def t = new Vertex("target")
        def e1 = new Edge(s, t)
        def e2 = new Edge(s, t)
        def e3 = new Edge(s, t, 0.0)
        def e4 = new Edge(t, s)

        then:
        e1.hashCode() == e2.hashCode()
        e2.hashCode() == e3.hashCode()
        e1.hashCode() != e4.hashCode()
    }

    def "test Edge#toString"() {
        when:
        def s = new Vertex("source")
        def t = new Vertex("target")
        def e1 = new Edge(s, t)

        then:
        e1.toString() == 'source-(0.0)->target'
    }

    def "test simple graph"() {

        when: "Create an empty graph"
        Graph g = new Graph()

        then: "graph is empty"
        g.getSources(null) == []
        g.getTargets(null) == []

        when:
        g.addEdge("a", "b")
        g.addEdge("b", "c", 1.0)

        then:
        g.toString() == "a-(0.0)->b, b-(1.0)->c"
    }

    def "test plugin dependency graph"() {

        given: "Create an empty graph"
        Graph g = new Graph()

        when: "add crmContact vertex"
        // add the vertices
        def crmContact = g.addVertex("crmContact")

        then:
        crmContact.obj == "crmContact"

        when: "add two different vertices"
        def v1 = g.addVertex("crmApartment")
        def v2 = g.addVertex("crmAgreement")

        then: "vertexes are not equal"
        v1 != v2

        when: "add a few more vertices"
        g.addVertex("crmTask")
        g.addVertex("crmTags")
        g.addVertex("crmNotes")
        g.addVertex("crmInvitation")
        g.addVertex("crmContent")
        g.addVertex("crmProduct")
        g.addVertex("crmProduct") // Adding the same vertex again should do nothing

        // add edges to create linking structure
        g.addEdge("crmApartment", "crmContact")
        g.addEdge("crmAgreement", "crmContact")
        g.addEdge("crmAgreement", "crmTask") // fake
        g.addEdge("crmApartment", "crmTask") // fake
        g.addEdge("crmProduct", "crmContact")
        g.addEdge("crmTask", "crmContact") // fake

        then: "check vertices dependency"
        g.getSources(g.getVertex("crmContact")).join(', ') == "crmApartment, crmAgreement, crmProduct, crmTask"
        g.getSources(g.getVertex("crmTask")).join(', ') == "crmAgreement, crmApartment"
        g.getTargets(g.getVertex("crmTask")).join(', ') == "crmContact"
        g.toString() == "crmApartment-(0.0)->crmContact, crmAgreement-(0.0)->crmContact, crmAgreement-(0.0)->crmTask, crmApartment-(0.0)->crmTask, crmProduct-(0.0)->crmContact, crmTask-(0.0)->crmContact, crmTags, crmNotes, crmInvitation, crmContent"
        g.collect { it.toString() }.join(', ') == "crmAgreement, crmApartment, crmProduct, crmTask, crmContent, crmInvitation, crmNotes, crmTags, crmContact"
    }
}
