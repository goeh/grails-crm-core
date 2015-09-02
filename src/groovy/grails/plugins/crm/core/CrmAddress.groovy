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
 * Embedded postal address.
 *
 * @author Goran Ehrsson
 *
 */
abstract class CrmAddress implements CrmMutableAddressInformation {

    String address1 // Street Name
    String address2
    String address3
    String postalCode
    String city
    String region
    String country
    String timezone
    Float latitude
    Float longitude

    static constraints = {
        address1(maxSize: 64, nullable: true, blank: false)
        address2(maxSize: 64, nullable: true, blank: false)
        address3(maxSize: 64, nullable: true, blank: false)
        postalCode(maxSize: 16, nullable: true, blank: false)
        city(maxSize: 64, nullable: true, blank: false)
        region(maxSize: 32, nullable: true, blank: false)
        country(maxSize: 64, nullable: true, blank: false)
        timezone(maxSize: 8, nullable: true, blank: false)
        latitude(nullable: true, min: -180f, max: 180f, precision: 10, scale: 6)
        longitude(nullable: true, min: -180f, max: 180f, precision: 10, scale: 6)
    }

    static transients = ['address', 'street', 'empty', 'dao']

    static final List BIND_WHITELIST = ['address1', 'address2', 'address3', 'postalCode', 'city', 'region', 'country', 'timezone', 'latitude', 'longitude'].asImmutable()

    Map toMap() {
        def map = [:]
        for (prop in BIND_WHITELIST) {
            def value = this[prop]
            if (value) {
                map[prop] = value
            }
        }
        return map
    }

    /**
     * Copy all address fields from this instance to a new instance.
     *
     * @return newly created (subclass of) CrmAddress instance.
     */
    CrmAddress copy() {
        def a = getClass().newInstance()
        this.toMap().each { k, v ->
            a."$k" = v
        }
        return a
    }

    /**
     * Copy all address fields from this instance to another address instance.
     * @param other the CrmAddress instance to copy property values to
     */
    void copyTo(CrmAddress other) {
        for (prop in BIND_WHITELIST) {
            other[prop] = this[prop]
        }
    }

    void copyFrom(final CrmAddressInformation source) {
        for(p in CrmAddressInformation.ADDRESS_PROPERTIES) {
            this[p] = source[p]
        }
    }

    @Override
    transient boolean isEmpty() {
        this.toMap().isEmpty()
    }

    transient String getStreet() {
        address1
    }

    transient String getAddress(boolean includePostalCode = true, String delimiter = ', ') {
        StringBuilder s = new StringBuilder()
        if (address1) {
            s << address1
        }
        if (address2) {
            if (s.length() > 0) {
                s << delimiter
            }
            s << address2
        }
        if (address3) {
            if (s.length() > 0) {
                s << delimiter
            }
            s << address3
        }
        if (postalCode && includePostalCode) {
            if (s.length() > 0) {
                s << delimiter
            }
            s << postalCode
        }
        if (city) {
            if (postalCode && includePostalCode) {
                s << ' '
            } else if (s.length() > 0) {
                s << delimiter
            }
            s << city
        }
        if (region) {
            if (s.length() > 0) {
                s << delimiter
            }
            s << region
        }
        if (country) {
            if (s.length() > 0) {
                s << delimiter
            }
            s << country
        }
        return s.toString()
    }

    String toString() {
        getAddress(true)
    }

    transient Map<String, Object> getDao() {
        ['address1', 'address2', 'address3', 'postalCode', 'city',
                'region', 'country', 'timezone', 'latitude', 'longitude'].inject([:]) { m, i ->
            def v = this."$i"
            if (v != null) {
                m[i] = v
            }
            m
        }
    }
}
