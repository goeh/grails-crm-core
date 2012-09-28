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

import grails.plugins.crm.core.TenantUtils

import javax.servlet.http.HttpServletResponse

/**
 * Allow clients to specify tenant in request URL.
 * A check is made to ensure that the authenticated user has access to the given tenant.
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmTenantFilters {

    def crmSecurityService

    def filters = {
        tenantCheck(uri: '/**') {
            before = {
                def tenant = params.long('tenant')
                if(session) {
                    if(tenant && crmSecurityService.isAuthenticated()) {
                        if (crmSecurityService.isValidTenant(tenant)) {
                            session.tenant = tenant
                        } else {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN)
                            return
                        }
                    } else {
                        tenant = session.tenant
                    }
                }
                TenantUtils.setTenant(tenant ?: 0L)
            }
        }
    }
}
