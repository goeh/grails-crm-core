/*
 * Copyright (c) 2012 Goran Ehrsson.
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
 * under the License.
 */

package grails.plugins.crm.core

class CrmSecurityServiceSpec extends grails.plugin.spock.IntegrationSpec {

    def crmSecurityService

    def "current user is nobody by default"() {
        expect:
        crmSecurityService.currentUser?.username == 'nobody'
    }

    void "user is authenticated by default"() {
        expect:
        crmSecurityService.authenticated
    }

    void "tenant is zero by default"() {
        when:
        def tenant = crmSecurityService.currentTenant
        then:
        tenant.id == 0
        tenant.name == 'Default Tenant'
        tenant.type == null
        tenant.owner == 'nobody'
    }

    void "getTenants"() {
        when:
        def list = crmSecurityService.tenants
        then:
        list.isEmpty() == false

        when:
        def tenant = list.find {it}
        then:
        tenant != null
        tenant.name == 'Default Tenant'
    }
}
