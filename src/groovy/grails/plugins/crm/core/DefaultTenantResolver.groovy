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

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import javax.servlet.http.HttpServletRequest

/**
 * Resolved tenant from request parameter 'tenant'.
 */
@CompileStatic
class DefaultTenantResolver implements CrmTenantResolver {

    @Override
    Long resolve(final HttpServletRequest request, final GrailsParameterMap params) {
        params.long('_tenant') ?: params.long('tenant')
    }
}
