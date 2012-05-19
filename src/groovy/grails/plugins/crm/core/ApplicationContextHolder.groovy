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

   void setApplicationContext(ApplicationContext applicationContext) {
       ctx = applicationContext
   }

   static ApplicationContext getApplicationContext() {
      getInstance().ctx
   }

   static Object getBean(String name) {
      TEST_BEANS[name] ?: getApplicationContext().getBean(name)
   }

   static GrailsApplication getGrailsApplication() {
      getBean('grailsApplication')
   }

   static ConfigObject getConfig() {
      getGrailsApplication().config
   }

   static ServletContext getServletContext() {
      getBean('servletContext')
   }

   static GrailsPluginManager getPluginManager() {
      getBean('pluginManager')
   }

   // For testing
   static void registerTestBean(String name, bean) {
      TEST_BEANS[name] = bean
   }

   // For testing
   static void unregisterTestBeans() {
      TEST_BEANS.clear()
   }
}
