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
    // the plugin dependency group
    def groupId = "grails.crm"
    // the plugin version
    def version = "0.9.7"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Grails Crm Core Plugin" // Headline display name of the plugin
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
Grails CRM Core Functionality.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/goeh/grails-crm-core"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Technipelago AB", url: "http://www.technipelago.se/" ]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "github", url: "https://github.com/goeh/grails-crm-core/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/goeh/grails-crm-core" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        crmSecurityDelegate(grails.plugins.crm.core.DummySecurityDelegate)
        applicationContextHolder(ApplicationContextHolder) { bean ->
            bean.factoryMethod = 'getInstance'
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
        if(applicationContext.containsBean("gormSelection")) {
            applicationContext.getBean("gormSelection").fixedCriteria = {query, params->
                eq('tenantId', TenantUtils.tenant)
            }
        } else {
            log.warn("selection plugin not installed")
        }
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
