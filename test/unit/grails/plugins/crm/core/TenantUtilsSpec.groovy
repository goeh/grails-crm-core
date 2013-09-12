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

import spock.lang.Specification

/**
 * Tests for TenantUtils.
 */
class TenantUtilsSpec extends Specification {

    def "test set and get"() {
        when:
        TenantUtils.setTenant(42L)

        then:
        TenantUtils.tenant == 42L

        when:
        TenantUtils.clear()

        then:
        TenantUtils.tenant == 0L

        when:
        def result = TenantUtils.withTenant(4L) { TenantUtils.tenant }

        then:
        result == 4L
        TenantUtils.tenant == 0L
    }
}
