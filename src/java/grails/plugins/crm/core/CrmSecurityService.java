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

package grails.plugins.crm.core;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

/**
 * This interface must be implemented by security service delegates.
 */
public interface CrmSecurityService {

    /**
     * Checks if the current user is authenticated in this session.
     *
     * @return
     */
    boolean isAuthenticated();

    /**
     * Checks if the current user has permission to perform an operation.
     *
     * @param permission wildcard permission
     * @return
     */
    boolean isPermitted(Object permission);

    /**
     * Execute a piece of code as a specific user.
     *
     * @param username
     * @param closure
     * @return
     */
    Object runAs(String username, Closure closure);

    /**
     * Create a new user.
     *
     * @param properties user domain properties.
     * @return info about newly created user (DAO)
     */
    Map<String, Object> createUser(Map<String, Object> properties);

    /**
     * Update an existing user.
     *
     * @param username username
     * @param properties key/value pairs to update
     * @return user information after update
     */
    Map<String, Object> updateUser(String username, Map<String, Object> properties);

    /**
     * Get the current user information.
     *
     * @return a Map with user properties (username, name, email, ...)
     */
    Map<String, Object> getCurrentUser();

    /**
     * Get user information for a user.
     *
     * @return a Map with user properties (username, name, email, ...)
     */
    Map<String, Object> getUserInfo(String username);

    /**
     * Delete a user.
     *
     * @param username username
     * @return true if the user was deleted
     */
    boolean deleteUser(String username);

    /**
     * Create new tenant.
     *
     * @param tenantName name of tenant
     * @param tenantType type of tenant
     * @param parent     optional parent tenant
     * @param owner      username of tenant owner
     * @return info about newly created tenant (DAO)
     */
    Map<String, Object> createTenant(String tenantName, String tenantType, Long parent, String owner);

    /**
     * Update tenant properties.
     *
     * @param tenantId id of tenant to update
     * @param properties key/value pairs to update
     * @return tenant information after update
     */
    Map<String, Object> updateTenant(Long tenantId, Map<String, Object> properties);

    /**
     * Get the current executing tenant.
     *
     * @return a Map with tenant properties (id, name, type, ...)
     */
    Map<String, Object> getCurrentTenant();

    /**
     * Get tenant information.
     *
     * @return a Map with tenant properties (id, name, type, ...)
     */
    Map<String, Object> getTenantInfo(Long id);

    /**
     * Return all tenants that a user owns.
     *
     * @param username username
     * @return list of tenants (DAO)
     */
    List<Map<String, Object>> getTenants(String username);

    /**
     * Check if current user can access the specified tenant.
     *
     * @param tenantId the tenant ID to check
     * @param username username or null for current user
     * @return true if user has access to the tenant (by it's roles, permissions or ownership)
     */
    boolean isValidTenant(Long tenantId, String username);

    /**
     * Delete a tenant and all information associated with the tenant.
     *
     * @param id tenant id
     * @return true if the tenant was deleted
     */
    boolean deleteTenant(Long id);

    Object hashPassword(String password, byte[] salt);

    byte[] generateSalt();
}
