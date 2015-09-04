/*
 * Copyright (c) 2015 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * Created by goran on 15-09-04.
 */
class CrmThemeSpec extends Specification {

    def "test toString"() {
        expect:
        new CrmTheme('foo', 42L).toString() == 'foo@42'
    }

    def "test null arguments"() {
        when:
        new CrmTheme(null, 42)

        then:
        ExceptionInInitializerError e1 = thrown()
        e1.message == "theme name must be specified"

        when:
        new CrmTheme('foo', null)

        then:
        ExceptionInInitializerError e2 = thrown()
        e2.message == "theme tenant must be specified"
    }

    def "test equals"() {
        when:
        def reference = new CrmTheme('foo', 42L)

        then:
        reference == reference
        reference == new CrmTheme('foo', 42L)
        reference != new CrmTheme('foo', 43L)
        reference != new CrmTheme('bar', 42L)
    }

    def "test hashCode"() {
        given:
        def key = new CrmTheme('foo', 42L)
        def map = [:]

        when:
        map.put(key, 'Hello World')

        then:
        map.get(key) == 'Hello World'

        when:
        map.remove(key)

        then:
        map.get(key) == null
    }
}
