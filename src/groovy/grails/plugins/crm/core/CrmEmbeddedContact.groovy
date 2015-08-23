/*
 * Copyright (c) 2014 Goran Ehrsson.
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

/**
 * Embedded contact information.
 */
class CrmEmbeddedContact extends CrmAddress implements CrmContactInformation, Serializable {
    String firstName
    String lastName
    String companyName
    Long companyId
    String title
    String telephone
    String email
    String number

    static constraints = {
        firstName(maxSize: 40, nullable: true)
        lastName(maxSize: 40, nullable: true)
        companyName(maxSize: 80, nullable: true)
        companyId(nullable: true)
        title(maxSize: 80, nullable: true)
        telephone(maxSize: 20, nullable: true)
        email(maxSize: 80, nullable: true, email: true)
        number(maxSize: 40, nullable: true)
    }

    static transients = ['id', 'name', 'fullName', 'fullAddress', 'addressInformation', 'dao'] + CrmAddress.transients

    CrmEmbeddedContact() {
    }

    CrmEmbeddedContact(CrmContactInformation contactInfo) {
        firstName = contactInfo.firstName
        lastName = contactInfo.lastName
        companyName = contactInfo.companyName
        title = contactInfo.title
        telephone = contactInfo.telephone
        email = contactInfo.email
        number = contactInfo.number

        def addr = contactInfo.getAddressInformation()
        if(addr != null) {
            address1 = addr.address1
            address2 = addr.address2
            address3 = addr.address3
            postalCode = addr.postalCode
            city = addr.city
            country = addr.country
        }
    }

    @Override
    String toString() {
        getFullName()
    }

    transient Long getId() {
        null
    }

    @Override
    transient String getName() {
        def s = new StringBuilder()
        if (firstName) {
            s << firstName
        }
        if (lastName) {
            if (s.length()) {
                s << ' '
            }
            s << lastName
        }
        s.toString()
    }

    @Override
    transient String getFullName() {
        def s = new StringBuilder()
        s << getName()
        if (companyName) {
            if (s.length()) {
                s << ', '
            }
            s << companyName
        }
        s.toString()
    }

    transient String getFullAddress() {
        getAddress(true)
    }

    @Override
    transient CrmAddressInformation getAddressInformation() {
        this
    }

    transient Map<String, Object> getDao() {
        def result = ['firstName', 'lastName', 'companyName', 'companyId', 'title', 'telephone', 'email', 'number'].inject(super.getDao()) { map, prop ->
            def v = this[prop]
            if (v != null) {
                map[prop] = v
            }
            map
        }
        result.name = getName()
        result.fullName = getFullName()
        result.fullAddress = getFullAddress()

        result
    }
}
