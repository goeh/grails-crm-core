/*
 * Copyright 2013 Goran Ehrsson.
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
 * Dummy security delegate that is used by integration tests.
 *
 * @author Goran Ehrsson
 * @since 1.0
 */
class TestSecurityDelegate implements CrmSecurityDelegate {

    def users = [:]
    def current = 'nobody'

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
        def restore = current
        def rval
        try {
            current = username
            rval = closure.call()
        } finally {
            current = restore
        }
        return rval
    }

    void createUser(String username, String password) {
        users[username] = password
    }

    void setPassword(String username, String password) {
        users[username] = password
    }

    String getCurrentUser() {
        current
    }

    boolean deleteUser(String username) {
        if (users[username]) {
            users.remove(username)
            return true
        }
        return false
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
