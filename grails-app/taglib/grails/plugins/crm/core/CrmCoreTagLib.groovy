/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package grails.plugins.crm.core

class CrmCoreTagLib {

    static namespace = "crm"

    def pluginManager
    def groovyPagesTemplateEngine
    def crmPluginService
    def crmSecurityService

    def noUser = {attrs, body ->
        def principal = crmSecurityService.getCurrentUser()
        if (!principal) {
            out << body()
        }
    }

    def user = {attrs, body ->
        def principal = crmSecurityService.getCurrentUser()
        if (principal) {
            out << body(principal as Map)
        }
    }

    def tenant = {attrs, body ->
        def tenant = crmSecurityService.currentTenant
        if(tenant) {
            out << body(tenant as Map)
        }
    }

    def hasPermission = {attrs, body->
        def perm = attrs.permission
        if(! perm) {
            throwTagError("Tag [hasPermission] is missing required attribute [permission]")
        }
        if(crmSecurityService.isPermitted(perm)) {
            out << body()
        }
    }

    def hasPlugin = {attrs, body ->
        def plugin = attrs.name
        if (!plugin) {
            out << "Tag [hasPlugin] missing required attribute [name]"
            return
        }
        if (pluginManager.hasGrailsPlugin(plugin)) {
            out << body()
        }
    }

    def formatBytes = {attrs ->
        def b = attrs.value
        if (b == null) {
            throwTagError("Tag [formatBytes] is missing required attribute [value]")
        }
        if (!(b instanceof Number)) {
            b = Integer.valueOf(b.toString())
        }
        out << WebUtils.bytesFormatted(b)
    }

    def decorate = {attrs, body ->
        out << WebUtils.decorateText(body().toString(), attrs.max ? Integer.valueOf(attrs.max) : 0)
    }

    def pluginViews = {attrs, body ->
        def location = attrs.location
        if (!location) {
            out << "Tag [pluginViews] missing required attribute [location]"
            return
        }
        def views = crmPluginService.getViews(controllerName, actionName, location).sort {it.index ?: (it.id ?: 99999)}
        if (views && attrs.tabs) {
            g.content(tag: "head") {
                """
<script type="text/javascript">
  <!--
  jQuery(document).ready(function() {
    \$("#content").crmTabs();
  });
  // -->
</script>
"""
            }
            // Display list of tabs.
            out << '<div class="panel"><ul class="tabs">'
            for (view in views) {
                def model = view.model
                if (model != null && model instanceof Closure) {
                    def cl = model.clone()
                    cl.delegate = new ClosureDelegate(delegate, grailsApplication, pageScope.getVariables(), [:])
                    cl.resolveStrategy = Closure.DELEGATE_FIRST
                    model = cl()
                }
                def dlg = new ClosureDelegate(delegate, grailsApplication, pageScope.getVariables(), model ?: [:])
                def label = view.label
                if (label != null && label instanceof Closure) {
                    def cl = label.clone()
                    cl.delegate = dlg
                    cl.resolveStrategy = Closure.DELEGATE_FIRST
                    label = cl()
                }
                def title = view.title
                if (title != null && title instanceof Closure) {
                    def cl = title.clone()
                    cl.delegate = dlg
                    cl.resolveStrategy = Closure.DELEGATE_FIRST
                    title = cl()
                }
                out << """<li title="${title ?: label}"><a href="#${view.id}">${label}</a></li>"""
            }
            out << """</ul><div class="clear"></div><div class="tab_container">"""
        }

        for (view in views) {
            def model = view.model
            if (model != null && model instanceof Closure) {
                def cl = model.clone()
                cl.delegate = new ClosureDelegate(delegate, grailsApplication, pageScope.getVariables(), [:])
                cl.resolveStrategy = Closure.DELEGATE_FIRST
                model = cl()
            }
            if (attrs.tabs) {
                out << """<div id="${view.id}" class="tab_content">"""
            }

            if (view.template) {
                out << render(template: view.template, plugin: view.plugin, model: model)
            } else if (view.text) {
                groovyPagesTemplateEngine.createTemplate(view.text, "view-${view.id ?: text.hashCode()}.gsp").make(model).writeTo(out)
            }
            if (attrs.tabs) {
                out << '</div>'
            }
        }
        if (attrs.tabs) {
            out << '</div></div>'
        }
    }
}
