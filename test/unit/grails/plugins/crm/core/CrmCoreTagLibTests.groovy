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
}
