package grails.plugins.crm.core

import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Test spec for ApplicationContextHolder.
 */
class ApplicationContextHolderSpec extends IntegrationSpec {

    def "test that we can get grailsApplication"() {
        expect:
        ApplicationContextHolder.getGrailsApplication() instanceof GrailsApplication
    }
}
