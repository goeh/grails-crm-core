/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package grails.plugins.crm.core

import java.security.MessageDigest

/**
 * Dummy delegate for CrmSecurityService that can be used for testing.
 * @author Goran Ehrsson
 * @since 0.1
 */
class DummySecurityDelegate {

    String username = "nobody"
    String name = "Nobody"
    String email = "nobody@unknown.net"
    String address1
    String address2
    String postalCode
    String city
    String countryCode
    String telephone
    boolean enabled = true

    Long tenant = 0L

    boolean isAuthenticated() {
        true
    }

    def runAs(String username, Closure closure) {
        closure.call()
    }

    def getCurrentUser() {
        ['username', 'name', 'email', 'address1', 'address2', 'postalCode', 'city', 'countryCode', 'telephone', 'enabled'].inject([:]) {map, p ->
            map[p] = this."$p"
            return map
        }
    }

    def getCurrentTenant() {
        [id: tenant, name: "Default Tenant", owner: username]
    }

    List getTenants() {
        [getCurrentTenant()]
    }
}
