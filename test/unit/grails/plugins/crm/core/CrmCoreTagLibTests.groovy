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

import grails.test.mixin.TestFor

@TestFor(CrmCoreTagLib)
class CrmCoreTagLibTests {

    void testUserIsNotAuthenticated() {
        def taglib = applicationContext.getBean(CrmCoreTagLib)
        taglib.crmSecurityService = [isAuthenticated: {false}, getCurrentUser: {[:]}]
        // Make sure tag returns nothing since we are not logged in.
        assert applyTemplate("<crm:user>\${username}</crm:user>") == ""
    }

    void testUserIsAuthenticated() {
        def taglib = applicationContext.getBean(CrmCoreTagLib)
        taglib.crmSecurityService = [isAuthenticated: {true}, getCurrentUser: {[username: "test", name: "Test User"]}]
        // Make sure tag returns the principal since we are logged in.
        assert applyTemplate("<crm:user>\${username}</crm:user>") == "test"
    }

    void testMissingPlugin() {
        def taglib = applicationContext.getBean(CrmCoreTagLib)
        taglib.pluginManager = this
        assert applyTemplate("<crm:hasPlugin name=\"cxf\">INSTALLED</crm:hasPlugin>") == ""
    }

    void testInstalledPlugin() {
        def taglib = applicationContext.getBean(CrmCoreTagLib)
        taglib.pluginManager = this
        assert applyTemplate("<crm:hasPlugin name=\"hibernate\">INSTALLED</crm:hasPlugin>") == "INSTALLED"
    }

    boolean  hasGrailsPlugin(String name) {
        name == 'hibernate'
    }

    void testDecorate() {
        def taglib = applicationContext.getBean(CrmCoreTagLib)
        assert applyTemplate('<crm:decorate max="20">The quick brown fox jumps over the lazy dog.</crm:decorate>') == "The quick brown f..."
        assert applyTemplate('<crm:decorate><script>alert("buuuh!")</script></crm:decorate>') == '&lt;script&gt;alert(&quot;buuuh!&quot;)&lt;/script&gt;'
    }
}
