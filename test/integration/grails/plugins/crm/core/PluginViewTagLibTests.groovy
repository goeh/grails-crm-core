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

import grails.test.GroovyPagesTestCase
import org.springframework.web.context.request.RequestContextHolder as RCH

/**
 * Test crm:pluginViews tag.
 */
class PluginViewTagLibTests extends GroovyPagesTestCase {

    def crmPluginService

    void testPluginViews() {
        def enabled
        crmPluginService.registerView('crmContact', 'show', 'tabs',
                [id: "friends", label: "Friends", template: '/crmContact/friends', visible:{enabled}, model: {
                    [result: [[name: "Joe Adams"], [name: "Liza Brown"], [name: "Alex Sherman"]], totalCount: 3]
                }]
        )
        def template = '<crm:pluginViews location="tabs" var="v"><g:each in="\${v.model.result}" var="f">\${f.name} </g:each>are \${v.id} of \${crmContact.name}</crm:pluginViews>'

        def params = RCH.currentRequestAttributes()
		params.controllerName = 'crmContact'
		params.actionName = 'show'
        enabled = false
        assert applyTemplate(template, [crmContact:[name:"Grails"]]) == ""

        enabled = true
        assert applyTemplate(template, [crmContact:[name:"Grails"]]) == "Joe Adams Liza Brown Alex Sherman are friends of Grails"
    }
}
