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

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.hibernate.Hibernate

/**
 * Grails CRM Core Services.
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmCoreService {

    static transactional = false

    def grailsApplication
    def crmFeatureService

    List getInstalledFeatures() {
        crmFeatureService ? crmFeatureService.getApplicationFeatures() : []
    }

    boolean hasFeature(String feature, String role = null, Long tenant = null) {
        if(crmFeatureService != null) {
            return crmFeatureService.hasFeature(feature, role, tenant)
        }
        return false
    }

    Map getFeature(String feature) {
        if(crmFeatureService != null) {
            return crmFeatureService.getFeature(feature)
        }
        return null
    }

    /**
     * Check if an object is a domain instance.
     * @param object the object to check
     * @return true if the object is a domain instance
     */
    boolean isDomainClass(object) {
        grailsApplication.isDomainClass(object.getClass()) && Hibernate.getClass(object)
    }

    /**
     * Find a domain class in application context.
     *
     * @param name full class name of domain or it's property name i.e. "crmContact"
     */
    Class getDomainClass(String name) {
        def domain = grailsApplication.domainClasses.find {it.propertyName == name}
        if (domain) {
            domain = domain.clazz
        } else {
            domain = grailsApplication.classLoader.loadClass(name)
        }
        return domain
    }

    String getReferenceIdentifier(object) {
        def ref
        if (isDomainClass(object)) {
            def instance = GrailsHibernateUtil.unwrapIfProxy(object)
            ref = GrailsNameUtils.getPropertyName(instance.class) + '@' + instance.ident()
        } else {
            ref = object.toString()
        }
        return ref
    }

    def getReference(String identifier) {
        def (name, key) = identifier.split('@').toList()
        if (name && key) {
            def domainClass = getDomainClass(name)
            if (domainClass) {
                return domainClass.get(key)
            }
        }
        return identifier
    }
}

