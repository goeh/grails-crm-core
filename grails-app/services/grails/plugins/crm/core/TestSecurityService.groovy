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
 * Dummy security service that is used by integration tests.
 *
 * @author Goran Ehrsson
 * @since 1.0
 */
class TestSecurityService implements CrmSecurityService {

    def tenants = [[id: 1L, name: "Default Tenant", user: [username: "nobody", email: "nobody@unknown.net"],
            options: [], dateCreated: new Date(), expires: (new Date() + 10)]]
    def user = [guid: "576793b8-106d-4d60-bb26-e953c874d501", username: "nobody", name: "Nobody", email: "nobody@unknown.net",
            enabled: true, timezone: TimeZone.getDefault(), roles: [], permissions: []]
    def aliases = [:]

    /**
     * Checks if the current user is authenticated in this session.
     * @return
     */
    boolean isAuthenticated() {
        true
    }

    /**
     * Checks if the current user has permission to perform an operation.
     * @param permission
     * @return
     */
    boolean isPermitted(Object permission) {
        true
    }

    /**
     * Execute a piece of code as a specific user.
     * @param username
     * @param closure
     * @return
     */
    def runAs(String username, Closure closure) {
        def restore = user.username
        try {
            user.username = username
            closure.call()
        } finally {
            user.username = restore
        }
    }

    /**
     * Create a new user.
     *
     * @param properties user domain properties.
     * @return
     */
    Map<String, Object> createUser(Map<String, Object> properties) {
        def user = getCurrentUser()
        event(for: "crm", topic: "userCreated", data:user)
        return user
    }

    /**
     * Update an existing user.
     *
     * @param username username
     * @param properties key/value pairs to update
     * @return user information after update
     */
    Map<String, Object> updateUser(String username, Map<String, Object> properties) {
        if(username == user.username) {
            user.putAll(properties)
            event(for: "crm", topic: "userUpdated", data:user)
        }
        return user
    }

    /**
     * Return information about the current executing user.
     * @return
     */
    Map<String, Object> getCurrentUser() {
        user
    }

    /**
     * Return information about any user.
     * @param username
     * @return
     */
    Map<String, Object> getUserInfo(String username) {
        return getCurrentUser()
    }

    boolean deleteUser(String username) {
        def user = getUserInfo(username)
        event(for: "crm", topic: "userDeleted", data:user)
        return true
    }

    /**
     * Create a new tenant.
     *
     * @param tenantName name of tenant
     * @param parent optional parent tenant
     * @param owner username of tenant owner
     * @param locale default locale for the tenant
     * @return
     */
    Map<String, Object> createTenant(String tenantName, Long parent = null, String owner = null, Locale locale = null) {
        def n = tenants.size() + 1
        def t = [id: n, name: "Tenant #$n", owner: "user$n"]
        tenants << t
        event(for: "crm", topic: "tenantCreated", data:t)
        return t
    }

    /**
     * Update tenant properties.
     *
     * @param tenantId id of tenant to update
     * @param properties key/value pairs to update
     * @return tenant information after update
     */
    Map<String, Object> updateTenant(Long tenantId, Map<String, Object> properties) {
        def tenant = getTenantInfo(tenantId)
        if(tenant) {
            tenant.putAll(properties)
        }
        return tenant
    }

    /**
     * Return information about the current tenant.
     * @return
     */
    Map<String, Object> getCurrentTenant() {
        tenants[-1]
    }

    /**
     * Return information about any tenant.
     *
     * @param id
     * @return
     */
    Map<String, Object> getTenantInfo(Long id) {
        tenants.find {it.id == id}
    }

    /**
     * Get a list of all tenant that a user owns.
     * @param username username
     * @return
     */
    List<Map<String, Object>> getTenants(String username) {
        tenants
    }

    /**
     * Check if a user has permission to acces a tenant.
     *
     * @param tenantId the tenant ID to check
     * @param username username or null for current user
     * @return
     */
    boolean isValidTenant(Long tenantId, String username = null) {
        tenants.find {it.id == tenantId} != null
    }

    /**
     * Execute a piece of code as a specific tenant.
     * @param tenantId
     * @param work the code to execute
     * @return the result of calling the work closure
     */
    def withTenant(Long tenantId, Closure work) {
        TenantUtils.withTenant(tenantId, work)
    }

    boolean deleteTenant(Long tenantId) {
        def tenant = getTenantInfo(tenantId)
        event(for: "crm", topic: "tenantDeleted", data: tenant)
        return true
    }

    void addPermissionToUser(String permission, String username, Long tenant) {
        user.permissions << permission
    }

    void addPermissionToRole(String permission, String roleName, Long tenant) {
        user.roles.get(roleName, [name:roleName, permissions:[]]).permissions << permission
    }

    void addPermissionAlias(String name, List<String> permissions) {
        aliases.get(name, []).addAll(permissions)
    }

    boolean removePermissionAlias(String name) {
        aliases.remove(name)
    }

    List<String> getPermissionAlias(String name) {
        aliases[name]?.asImmutable()
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
