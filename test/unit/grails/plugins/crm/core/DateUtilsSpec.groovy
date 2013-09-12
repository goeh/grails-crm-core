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
