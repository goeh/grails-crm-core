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
