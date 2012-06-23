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

class CrmSecurityServiceSpec extends grails.plugin.spock.IntegrationSpec {

    def crmSecurityService

    def "current user is nobody by default"() {
        expect:
        crmSecurityService.currentUser?.username == 'nobody'
    }

    def "user is authenticated by default"() {
        expect:
        crmSecurityService.authenticated
    }

    def "user is allowed everything by default"() {
        expect:
        crmSecurityService.isPermitted("test:index")
    }

    def "run as admin"() {
        given:
        def u = null
        when:
        crmSecurityService.runAs("admin") {
            u = crmSecurityService.currentUser?.username
        }
        then:
        u == "admin"
    }

    def "tenant is one by default"() {
        when:
        def tenant = crmSecurityService.currentTenant
        then:
        tenant.id == 1
        tenant.name == 'Default Tenant'
        tenant.type == null
        tenant.owner == 'nobody'
    }

    def "check if tenant is valid"() {
        expect:
        crmSecurityService.isValidTenant(1)
    }

    def "getTenants"() {
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

    def "test with another tenant"() {
        given:
        def t = null
        when:
        crmSecurityService.withTenant(1) {
            t = crmSecurityService.currentTenant?.id
        }
        then:
        t == 1
    }
}
