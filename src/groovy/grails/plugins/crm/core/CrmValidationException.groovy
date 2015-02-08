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
 * Exception class thrown by services when data binding/validation fails.
 */
class CrmValidationException extends RuntimeException {

    Object[] domainInstances

    CrmValidationException(String message, Object[] args) {
        super(message)
        domainInstances = new Object[args.length]
        for (int i = 0; i < args.length; i++) {
            domainInstances[i] = args[i]
        }
    }

    /**
     * Return single domain instance.
     *
     * @return
     */
    Object getDomainInstance() {
        if (domainInstances.length > 0) {
            return domainInstances[0]
        }
        return null
    }

    /**
     * Return domain instance at index.
     *
     * @param idx
     * @return
     */
    Object getAt(int idx) {
        idx < domainInstances.length ? domainInstances[idx] : null
    }

    /**
     * Number of domain instances.
     *
     * @return
     */
    int size() {
        domainInstances.length
    }

    /**
     * Iterate over all domain instances.
     *
     * @return
     */
    Iterator<Object> iterator() {
        domainInstances.iterator()
    }
}
