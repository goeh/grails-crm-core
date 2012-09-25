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
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.util.WebUtils

/**
 * Dummy security service that is used by integration tests.
 *
 * @author Goran Ehrsson
 * @since 1.0
 */
class TestSecurityService {

    def tenants = [[id: 1L, name: "Default Tenant", user: [username: "nobody", email: "nobody@unknown.net"],
            options: [], dateCreated: new Date(), expires: (new Date() + 10)]]
    def users = [admin: [guid: "630b87ee-df20-4791-98db-2fc3f290ed0f", username: "admin", name: "Administrator", email: "admin@unknown.net",
            enabled: true, timezone: TimeZone.getDefault(), roles: ['admin'], permissions: ['*:*']],
            nobody: [guid: "576793b8-106d-4d60-bb26-e953c874d501", username: "nobody", name: "Nobody", email: "nobody@unknown.net",
                    enabled: true, timezone: TimeZone.getDefault(), roles: [], permissions: []]
    ]
    def aliases = [:]

    CrmSecurityDelegate crmSecurityDelegate

    /**
     * Checks if the current user is authenticated in this session.
     * @return
     */
    boolean isAuthenticated() {
        crmSecurityDelegate.isAuthenticated()
    }

    /**
     * Checks if the current user has permission to perform an operation.
     * @param permission
     * @return
     */
    boolean isPermitted(Object permission) {
        crmSecurityDelegate.isPermitted(permission)
    }

    /**
     * Execute a piece of code as a specific user.
     * @param username
     * @param closure
     * @return
     */
    def runAs(String username, Closure closure) {
        crmSecurityDelegate.runAs(username, closure)
    }


    // ==============================================================================================

    /**
     * Create a new user.
     *
     * @param properties user domain properties.
     * @return
     */
    Map<String, Object> createUser(Map<String, Object> properties) {
        crmSecurityDelegate.createUser(properties.username, properties.password)
        users[properties.username] = [guid: UUID.randomUUID().toString(), username: properties.username, name: properties.name, email: properties.email,
                enabled: properties.enabled ? true : false, timezone: TimeZone.getDefault(), roles: [], permissions: []]
        def user = getUserInfo(properties.username)
        event(for: "crm", topic: "userCreated", data: user)
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
        def map = users[username]
        if (!map) {
            throw new IllegalArgumentException("User [$username] not found")
        }
        def password = properties.remove('password')
        properties.each {key, value ->
            if (map.containsKey(key)) {
                map[key] = value
            }
        }
        if (password) {
            crmSecurityDelegate.setPassword(properties.username, properties.password)
        }
        event(for: "crm", topic: "userUpdated", data: map)
        return map
    }

    /**
     * Return information about the current executing user.
     * @return
     */
    Map<String, Object> getCurrentUser() {
        users[crmSecurityDelegate.getCurrentUser()]
    }

    /**
     * Return information about any user.
     * @param username
     * @return
     */
    Map<String, Object> getUserInfo(String username) {
        users[username]
    }

    boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            def map = users.remove(username)
            crmSecurityDelegate.deleteUser(username)
            event(for: "crm", topic: "userDeleted", data: map)
            return true
        }
        return false
    }

    /**
     * Create a new tenant.
     *
     * @param tenantName name of tenant
     * @param params optional parameters/options
     * @param initializer code that is executed after the tenant has been created, but before event 'tenantCreated' fires
     * @return info about newly created tenant (DAO)
     */
    Map<String, Object> createTenant(String tenantName, Map<String, Object> params = [:], Closure initializer = null) {
        def n = tenants.size() + 1
        def t = [id: n, name: "Tenant #$n", owner: user]
        tenants << t
        event(for: "crm", topic: "tenantCreated", data: t)
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
        if (tenant) {
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
        def map = users[username]
        if (map) {
            map.get('permissions', []) << permission
        }
    }

    void addPermissionToRole(String permission, String roleName, Long tenant) {
        // Not implemented
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

    void alert(HttpServletRequest request, String topic, String message = null) {
        def msg = "SECURITY ALERT! $topic $message [uri=${WebUtils.getForwardURI(request)}, ip=${request.remoteAddr}, tenant=${TenantUtils.tenant}, session=${request.session?.id}]"
        log.warn msg
        println msg
    }

    def hashPassword(String password, byte[] salt) {
        crmSecurityDelegate.hashPassword(password, salt)
    }

    byte[] generateSalt() {
        crmSecurityDelegate.generateSalt()
    }

}
