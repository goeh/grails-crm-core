/*
 * Copyright 2013 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.core

import spock.lang.Specification
import groovy.time.TimeDuration

import java.text.ParseException

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

    def "parse SQL date"() {
        expect:
        DateUtils.parseSqlDate('2014-06-02') instanceof java.sql.Date
    }

    def "format date"() {
        given:
        def timezone = TimeZone.getTimeZone("Europe/London")

        expect:
        DateUtils.format(createDate(2014, 6, 2, timezone), 'yyyy-MM-dd', timezone) == '2014-06-02'
        DateUtils.formatDate(createDate(2014, 6, 2, timezone), timezone) == '2014-06-02'
        DateUtils.formatDate('20140602', timezone) == '2014-06-02'
        DateUtils.formatDate('20140631', timezone) == '20140631!' // Note the ! at the end
    }

    def "parse Swedish date format"() {
        given:
        def timezone = TimeZone.getTimeZone("Europe/London")

        expect:
        DateUtils.parseDate(null, timezone) == null
        DateUtils.parseDate('', timezone) == null
        DateUtils.parseDate('2014-06-02', timezone) == createDate(2014, 6, 2, timezone)
    }

    def "parse US date format"() {
        given:
        def timezone = TimeZone.getTimeZone("US/Pacific")

        expect:
        DateUtils.parseDate('6/2/14', timezone) == createDate(2014, 6, 2, timezone)
    }

    def "parse invalid date"() {
        when:
        DateUtils.parseDate('6/31/14')

        then:
        thrown(ParseException)
    }

    def "test isBefore"() {
        expect:
        DateUtils.isBefore(new Date(), new Date() + 7)
        DateUtils.isBefore(new Date(), new Date() + 1)

        !DateUtils.isBefore(new Date(), new Date())
        !DateUtils.isBefore(new Date(), new Date() - 1)
    }

    def "test getFirstDayOfWeek"() {
        expect:
        DateUtils.getFirstDayOfWeek(new Locale('sv_SE'), TimeZone.getTimeZone("Europe/Stockholm")) == 1
        DateUtils.getFirstDayOfWeek(new Locale('en_US'), TimeZone.getTimeZone("US/Pacific")) == 1
    }

    private Date createDate(int y, int m, int d, TimeZone timezone) {
        Calendar cal = Calendar.getInstance(timezone, new Locale("en_US"))
        cal.clearTime()
        cal.set(Calendar.YEAR, y)
        cal.set(Calendar.MONTH, m - 1)
        cal.set(Calendar.DAY_OF_MONTH, d)
        cal.getTime()
    }
}
