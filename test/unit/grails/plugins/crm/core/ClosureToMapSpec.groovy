/*
 * Copyright 2012 Goran Ehrsson.
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

/**
 * Tests for the ClosureToMap utility class.
 */
class ClosureToMapSpec extends Specification {

    def "1-level Map"() {
        when:
        def map = ClosureToMap.convert {
            oneString "A"
            listOfStrings "A", "B"
            aMap foo:42, bar:4711
        }

        then:
        map.oneString == "A"
        map.listOfStrings.size() == 2
        map.listOfStrings == ["A", "B"]
        map.aMap.size() == 2
        map.aMap == [foo:42, bar:4711]
    }

    def "2-level Map"() {
        when:
        def map = ClosureToMap.convert {
            volvo {
                model "V70"
                brakes {
                    frontLeft 0.9
                    frontRight 1.0
                    rearLeft 0.9
                    rearRight 0.8
                }
            }
            ford {
                model "Focus"
                brakes {
                    frontLeft 1.1
                    frontRight 1.0
                    rearLeft 0.9
                    rearRight 1.0
                }
            }
        }

        then:
        map.size() == 2
        map.volvo.model == "V70"
        map.ford.model == "Focus"
        map.ford.brakes.frontLeft == 1.1
        println "$map"
    }
}
