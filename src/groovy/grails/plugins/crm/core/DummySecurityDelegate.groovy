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

import java.security.MessageDigest

/**
 * Dummy delegate for CrmSecurityService that can be used for testing.
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class DummySecurityDelegate implements SecurityServiceDelegate {

    def tenants = [[id: 1L, name: "Default Tenant", owner: "nobody"]]
    def user = [guid: "576793b8-106d-4d60-bb26-e953c874d501", username: "nobody", name: "Nobody", email: "nobody@unknown.net",
            enabled: true, timezone: TimeZone.getDefault(), roles: [], permissions: []]

    boolean isAuthenticated() {
        true
    }

    boolean isPermitted(Object permission) {
        true
    }

    def runAs(String username, Closure closure) {
        def restore = user.username
        try {
            user.username = username
            closure.call()
        } finally {
            user.username = restore
        }
    }

    Map<String, Object> createUser(Map<String, Object> properties) {
        // Ignore request.
        getCurrentUser()
    }

    Map getCurrentUser() {
        user
    }

    Map getUserInfo(String username) {
        return getCurrentUser()
    }

    Map<String, Object> createTenant(String tenantName, String tenantType, Long parent, String owner) {
        def n = tenants.size() + 1
        def t = [id: n, name: "Tenant #$n", owner: "user$n"]
        tenants << t
        return t
    }

    Map<String, Object> getCurrentTenant() {
        tenants[-1]
    }

    Map<String, Object> getTenantInfo(Long id) {
        tenants.find{it.id == id}
    }

    List<Map<String, Object>> getTenants(String username) {
        tenants
    }

    boolean isValidTenant(String username, Long tenantId) {
        tenants.find {it.id == tenantId} != null
    }

    private static final int hashIterations = 1000

    def hashPassword(String password, byte[] salt) {
        //password.encodeAsSHA256()
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset()
        digest.update(salt)
        byte[] input = digest.digest(password.getBytes("UTF-8"))
        for (int i = 0; i < hashIterations; i++) {
            digest.reset()
            input = digest.digest(input)
        }
        return input.encodeHex().toString()
    }

    byte[] generateSalt() {
        byte[] buf = new byte[128]
        new Random(System.currentTimeMillis()).nextBytes(buf)
        return buf
    }
}
