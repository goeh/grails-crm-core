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

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import javax.servlet.http.HttpServletRequest

/**
 * A tenant resolver implementation that uses a fixed tenant ID from configuration.
 * In Config.groovy: 'crm.tenant.fixed = 1L'
 */
class FixedTenantResolver implements CrmTenantResolver {

    private Long fixedTenant

    def grailsApplication

    FixedTenantResolver() {}

    FixedTenantResolver(Long tenant) {
        fixedTenant = tenant
    }

    @Override
    Long resolve(HttpServletRequest request, GrailsParameterMap params) {
        if (fixedTenant == null) {
            fixedTenant = grailsApplication.config.crm.tenant.fixed ?: 0L
        }
        // We trust fixed tenants, therefore it's ok to assign it to the session.
        if(request.session) {
            request.session.tenant = fixedTenant
        }
        fixedTenant
    }
}
