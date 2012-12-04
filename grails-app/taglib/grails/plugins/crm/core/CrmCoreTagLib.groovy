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

class CrmCoreTagLib {

    static namespace = "crm"

    def pluginManager
    def crmCoreService
    def crmPluginService
    def crmSecurityService

    def hasPlugin = { attrs, body ->
        def plugin = attrs.name
        if (!plugin) {
            out << "Tag [hasPlugin] missing required attribute [name]"
            return
        }
        if (pluginManager.hasGrailsPlugin(plugin)) {
            out << body()
        }
    }

    def formatBytes = { attrs ->
        def b = attrs.value
        if (b == null) {
            throwTagError("Tag [formatBytes] is missing required attribute [value]")
        }
        if (!(b instanceof Number)) {
            b = Integer.valueOf(b.toString())
        }
        out << WebUtils.bytesFormatted(b)
    }

    def decorate = { attrs, body ->
        def result = WebUtils.decorateText(body().toString().trim(), attrs.max ? Integer.valueOf(attrs.max) : 0)
        if (attrs.encode) {
            result = result."encodeAs${attrs.encode}"()
        } else {
            result = result.encodeAsHTML()
        }
        out << result
    }

    def referenceLink = { attrs, body ->
        def ref = attrs.remove('reference')
        if (!ref) {
            out << "Tag [referenceLink] missing required attribute [reference]"
            return
        }
        def s = body().trim() ?: ref.toString()
        ref = crmCoreService.getReferenceIdentifier(ref)
        def (ctrl, id) = ref.split('@').toList()
        def linkParams = [controller: ctrl, action: 'show', id: id]
        linkParams.putAll(attrs as Map)
        out << g.link(linkParams, s)
    }

    def pluginViews = { attrs, body ->

        def location = attrs.location
        if (!location) {
            out << "Tag [pluginViews] missing required attribute [location]"
            return
        }

        def views = crmPluginService.getViews(controllerName, actionName, location).sort { it.index ?: (it.id ?: 99999) }

        for (view in views) {
            def params = [:]
            params.putAll(view)

            def perm = view['permission']
            if (perm && !crmSecurityService?.isPermitted(perm)) {
                continue
            }

            // isVisible is closure or null/false
            def isVisible = view['visible']
            def closureDelegate
            if (isVisible instanceof Closure) {
                isVisible = isVisible.clone()
                if (closureDelegate == null) {
                    closureDelegate = new ClosureDelegate(delegate, grailsApplication, pageScope.getVariables(), [
                            session: session,
                            request: request,
                            controllerName: controllerName,
                            actionName: actionName,
                            flash: flash,
                            params: params
                    ])
                }
                isVisible.delegate = closureDelegate
                isVisible.resolveStrategy = Closure.DELEGATE_FIRST
            }

            if ((isVisible == null) || (isVisible == true) || isVisible.call()) {
                def model = params.model
                if (model instanceof Closure) {
                    def cl = model.clone()
                    if (closureDelegate == null) {
                        closureDelegate = new ClosureDelegate(delegate, grailsApplication, pageScope.getVariables(), [
                                session: session,
                                request: request,
                                controllerName: controllerName,
                                actionName: actionName,
                                flash: flash,
                                params: params
                        ])
                    }
                    cl.delegate = closureDelegate
                    cl.resolveStrategy = Closure.DELEGATE_FIRST
                    params.model = cl()
                }
                out << body([(attrs.var ?: 'it'): params])
            }
        }
    }
}
