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
import grails.plugins.crm.core.ApplicationContextHolder

/**
 * Grails CRM Core Plugin.
 */
class CrmCoreGrailsPlugin {
    def groupId = "grails.crm"
    def version = "1.0.7-SNAPSHOT"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/services/grails/plugins/crm/core/TestSecurityService.groovy",
            "src/groovy/grails/plugins/crm/core/TestSecurityDelegate.groovy"
    ]
    def title = "Grails CRM Core Plugin"
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
Grails CRM Core Functionality.
'''
    def documentation = "https://github.com/goeh/grails-crm-core"
    def license = "APACHE"
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]
    def issueManagement = [system: "github", url: "https://github.com/goeh/grails-crm-core/issues"]
    def scm = [url: "https://github.com/goeh/grails-crm-core"]

    def doWithSpring = {
        applicationContextHolder(ApplicationContextHolder) { bean ->
            bean.factoryMethod = 'getInstance'
        }
        currentTenant(grails.plugins.crm.core.CurrentTenantThreadLocal)
    }

    def doWithApplicationContext = { applicationContext ->
        if (applicationContext.containsBean("gormSelection")) {
            // TODO Hack warning: soft reference to selection plugin
            applicationContext.getBean("gormSelection").fixedCriteria = {query, params ->
                eq('tenantId', TenantUtils.tenant)
            }
        } else {
            log.warn("selection plugin not installed")
        }
    }

}
