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
 * Test spec for CrmAddress and CrmEmbeddedAddress.
 */
class CrmAddressSpec extends Specification {

    def "transient properties"() {
        when:
        def a1 = new CrmEmbeddedAddress(address1: "Short Street 2", postalCode: "12345", city: "Smallville")

        then:
        !a1.isEmpty()
        a1.street == a1.address1
        a1.toString() == 'Short Street 2, 12345 Smallville'
        a1.dao.address1 == 'Short Street 2'
    }

    def "copy address"() {
        given:
        def a1 = new CrmEmbeddedAddress(address1: "Short Street 2", postalCode: "12345", city: "Smallville")
        def a2 = new CrmEmbeddedAddress()
        def a3 = new CrmEmbeddedAddress(a1)
        def a4 = a1.copy()

        when:
        a1.copyTo(a2)

        then:
        a1.address1 == a1.address1
        a2.address1 == a1.address1
        a3.address1 == a1.address1
        a4.address1 == a1.address1

        a1.postalCode == a1.postalCode
        a2.postalCode == a1.postalCode
        a3.postalCode == a1.postalCode
        a4.postalCode == a1.postalCode

        a1.city == a1.city
        a2.city == a1.city
        a3.city == a1.city
        a4.city == a1.city
    }
}
