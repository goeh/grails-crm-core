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
 * Test spec for CrmEmbeddedContact.
 */
class CrmEmbeddedContactSpec extends Specification {
    def "transient properties"() {
        when:
        def contact = new CrmEmbeddedContact(firstName: "Joe", lastName: "Average", companyName: "ACME Inc.",  address1: "Short Street 2", postalCode: "12345", city: "Smallville")

        then:
        !contact.isEmpty()
        contact.street == contact.address1
        contact.name == 'Joe Average'
        contact.fullName == 'Joe Average, ACME Inc.'
        contact.toString() == contact.fullName
        contact.address == 'Short Street 2, 12345 Smallville'
        contact.fullAddress == contact.address
    }

    def "dao properties"() {
        when:
        def contact = new CrmEmbeddedContact(firstName: "Joe", lastName: "Average", companyName: "ACME Inc.",  address1: "Short Street 2", postalCode: "12345", city: "Smallville")

        then:
        contact.dao.name == 'Joe Average'
        contact.dao.fullName == 'Joe Average, ACME Inc.'
        contact.dao.fullAddress == 'Short Street 2, 12345 Smallville'
        contact.dao.address1 == 'Short Street 2'
    }
}
