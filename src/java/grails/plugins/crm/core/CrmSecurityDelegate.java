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

import java.util.Map;

/**
 * This interface must be implemented by security service delegates.
 */
public interface CrmSecurityDelegate {

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
     * Create a new user.
     *
     * @param username username of user
     * @param password wanted password
     */
    void createUser(String username, String password);

    /**
     * Change password for a user.
     *
     * @param username username of user
     * @param password new password
     */
    void setPassword(String username, String password);

    /**
     * Return username of current authenticated user.
     * @return username
     */
    String getCurrentUser();

    /**
     * Delete a user from the system.
     *
     * @param username username to delete
     * @return true if user was deleted
     */
    boolean deleteUser(String username);

    /**
     * Execute a piece of code as a specific user.
     *
     * @param username user to run as
     * @param closure the work to perform
     * @return whatever the closure returns
     */
    Object runAs(String username, Closure closure);

    Object hashPassword(String password, byte[] salt);

    byte[] generateSalt();
}
