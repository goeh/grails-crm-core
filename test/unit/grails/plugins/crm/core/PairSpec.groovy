package grails.plugins.crm.core

import spock.lang.Specification

/**
 * Unit tests for the Pair class.
 */
class PairSpec extends Specification {

    def "test getAt"() {
        given:
        def pair = new Pair(1, 2)
        def (left, right) = pair

        expect:
        pair.left == 1
        pair.right == 2
        pair.left == left
        pair.right == right
        pair[0] == 1
        pair[1] == 2

        when:
        pair[2]

        then:
        thrown(IndexOutOfBoundsException)
    }
}
