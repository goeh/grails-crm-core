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

import grails.plugins.crm.core.TenantUtils
import grails.plugins.crm.core.ApplicationContextHolder

/**
 * GR8 CRM Core Plugin.
 */
class CrmCoreGrailsPlugin {
    def groupId = "gr8crm"
    def version = "2.0.0"
    def grailsVersion = "2.2 > *"
    def dependsOn = [:]
    def loadAfter = ['controllers']
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/domain/grails/plugins/crm/core/TestLookupEntity.groovy",
            "grails-app/services/grails/plugins/crm/core/TestSecurityService.groovy",
            "src/groovy/grails/plugins/crm/core/TestSecurityDelegate.groovy"
    ]
    def title = "GR8 CRM Core Plugin"
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
GR8 CRM Core Functionality. See http://gr8crm.github.io for more information.
'''
    def documentation = "http://gr8crm.github.io/plugins/crm-core/crm-core.html"
    def license = "APACHE"
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]
    def issueManagement = [system: "github", url: "https://github.com/goeh/grails-crm-core/issues"]
    def scm = [url: "https://github.com/goeh/grails-crm-core"]

    def doWithSpring = {
        applicationContextHolder(ApplicationContextHolder) { bean ->
            bean.factoryMethod = 'getInstance'
        }
        crmTenantResolver(grails.plugins.crm.core.DefaultTenantResolver)
        currentTenant(grails.plugins.crm.core.CurrentTenantThreadLocal)
        customPropertyEditorRegistrar(grails.plugins.crm.core.CustomPropertyEditorRegistrar)
    }

    def doWithApplicationContext = { applicationContext ->
        if (applicationContext.containsBean("gormSelection")) {
            // TODO Soft reference to selection plugin is kind of ugly.
            applicationContext.getBean("gormSelection").fixedCriteria = { query, params ->
                if (targetClass.metaClass.respondsTo(null, 'getTenantId')) {
                    eq('tenantId', TenantUtils.tenant)
                }
            }
        } else {
            log.warn("selection plugin not installed")
        }
    }

    // TODO Is this really working, does it affect order of CrmTenantFilters???
    def getWebXmlFilterOrder() {
        def FilterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
        [CrmTenantFilters: FilterManager.GRAILS_WEB_REQUEST_POSITION + 10]
    }
}
