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
class CrmEmbeddedContact implements CrmContactInformation, Serializable {
    String firstName
    String lastName
    String companyName
    String title
    String address
    String telephone
    String email
    String number

    static constraints = {
        firstName(maxSize: 40, nullable: true)
        lastName(maxSize: 40, nullable: true)
        companyName(maxSize: 80, nullable: true)
        title(maxSize: 80, nullable: true)
        address(maxSize: 100, nullable: true)
        telephone(maxSize: 20, nullable: true)
        email(maxSize: 80, nullable: true, email: true)
        number(maxSize: 40, nullable: true)
    }

    static transients = ['id', 'name', 'fullName', 'fullAddress', 'dao']

    @Override
    String toString() {
        getFullName()
    }

    transient Long getId() {
        null
    }

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
        address
    }

    transient Map<String, Object> getDao() {
        ['firstName', 'lastName', 'companyName', 'title', 'address', 'telephone', 'email', 'number'].inject([:]) { map, prop ->
            def v = this[prop]
            if (v != null) {
                map[prop] = v
            }
            map
        }
    }
}
