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

/**
 * Grails CRM Authentication and Authorization Services.
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmSecurityService {

    def crmSecurityDelegate

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
    boolean isPermitted(permission) {
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

    Map createUser(properties) {
        crmSecurityDelegate.createUser(properties)
    }

    /**
     * Get the current user information.
     * @return a Map with user properties (username, name, email, ...)
     */
    Map getCurrentUser() {
        crmSecurityDelegate.getCurrentUser()
    }

    Map getUserInfo(String username) {
        crmSecurityDelegate.getUserInfo(username)
    }

    Map createTenant(String tenantName, String tenantType, Long parent = null, String owner = null) {
        if(! owner) {
            owner = crmSecurityDelegate.currentUser?.username
        }
        crmSecurityDelegate.createTenant(tenantName, tenantType, parent, owner)
    }

    /**
     * Get the current executing tenant.
     * @return a Map with tenant properties (id, name, type, ...)
     */
    Map getCurrentTenant() {
        crmSecurityDelegate.getCurrentTenant()
    }

    Map getTenantInfo(Long id) {
        crmSecurityDelegate.getTenantInfo(id)
    }

    /**
     * Return all tenants that the current user owns.
     * @return list of tenants (DAO)
     */
    List getTenants() {
        def username = crmSecurityDelegate.currentUser?.username
        username ? crmSecurityDelegate.getTenants(username) : []
    }

    /**
     * Check if current user can access the specified tenant.
     * @param tenantId the tenant ID to check
     * @return true if user has access to the tenant (by it's roles, permissions or ownership)
     */
    boolean isValidTenant(Long tenantId) {
        def username = crmSecurityDelegate.currentUser?.username
        username ? crmSecurityDelegate.isValidTenant(username, tenantId) : false
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

    def hashPassword(String password, byte[] salt) {
        crmSecurityDelegate.hashPassword(password, salt)
    }

    byte[] generateSalt() {
        crmSecurityDelegate.generateSalt()
    }

}
