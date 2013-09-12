/*
 * Copyright 2013 Goran Ehrsson.
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

import groovy.transform.CompileStatic

import javax.servlet.ServletContext

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Application local holder for static fields.
 *
 * Using Grails *Holder classes should be avoided, but when all else fails and you need to access
 * a common static field this utility class is a safer way than using Grails (deprecated) *Holder classes.
 * The reason this works in your application but not in Grails is that the static fields in your application
 * are only accessed within your application.
 * By using shared jars, multiple applications share static state held by shared classes
 * The original author of this code is Burt Beckwith in his blog post http://burtbeckwith.com/blog/?p=1017
 *
 * @author Burt Beckwith
 */
@Singleton
class ApplicationContextHolder implements ApplicationContextAware {

    private ApplicationContext ctx

    private static final Map<String, Object> TEST_BEANS = [:]

    @CompileStatic
    void setApplicationContext(final ApplicationContext applicationContext) {
        ctx = applicationContext
    }

    @CompileStatic
    static ApplicationContext getApplicationContext() {
        getInstance().ctx
    }

    // TODO Adding @CompileStatic to this method breaks ApplicationContext#getBean(String)
    static Object getBean(final String name) {
        TEST_BEANS[name] ?: getApplicationContext().getBean(name)
    }

    @CompileStatic
    static GrailsApplication getGrailsApplication() {
        final Object app = getBean('grailsApplication')
        if (app instanceof GrailsApplication) {
            return (GrailsApplication)app
        }
        throw new IllegalStateException("Illegal type for bean 'grailsApplication': ${app.class.name}")
    }

    @CompileStatic
    static ConfigObject getConfig() {
        getGrailsApplication().config
    }

    @CompileStatic
    static ServletContext getServletContext() {
        final Object ctx = getBean('servletContext')
        if (ctx instanceof ServletContext) {
            return (ServletContext)ctx
        }
        throw new IllegalStateException("Illegal type for bean 'servletContext': ${ctx.class.name}")
    }

    @CompileStatic
    static GrailsPluginManager getPluginManager() {
        final Object mgr = getBean('pluginManager')
        if (mgr instanceof GrailsPluginManager) {
            return (GrailsPluginManager)mgr
        }
        throw new IllegalStateException("Illegal type for bean 'pluginManager': ${mgr.class.name}")
    }

    // For testing
    @CompileStatic
    static void registerTestBean(final String name, final Object bean) {
        TEST_BEANS[name] = bean
    }

    // For testing
    @CompileStatic
    static void unregisterTestBeans() {
        TEST_BEANS.clear()
    }
}
