package grails.plugins.crm.core

import spock.lang.Specification
import groovy.time.TimeDuration

/**
 * Test the DateUtils class.
 */
class DateUtilsSpec extends Specification {

    def "format Groovy duration"() {
        expect:
        DateUtils.formatDuration(new TimeDuration(2, 30, 0, 0)) == '2t 30m'
        DateUtils.formatDuration(new TimeDuration(0, 10, 15, 0)) == '10m'
        DateUtils.formatDuration(new TimeDuration(0, 10, 15, 0), null, true) == '10m 15s'
    }
}
