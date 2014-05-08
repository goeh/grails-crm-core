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

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Grails CRM Core Services.
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmCoreService {

    static transactional = false

    private static final Pattern DOMAIN_REFERENCE_PATTERN = ~/^(\w+)@(\d+)$/

    def grailsApplication
    def crmFeatureService // TODO Remove dependency on external plugin!

    /**
     * Returns a list of all installed application features.
     * @return List of features
     */
    List getInstalledFeatures() {
        crmFeatureService ? crmFeatureService.getApplicationFeatures() : []
    }

    /**
     * Check if a feature is enabled in the current tenant
     *
     * @param feature name of feature
     * @param role if non-null check if feature is enabled for that role
     * @param tenant tenant to check in or null/omitted for current tenant
     * @return
     */
    boolean hasFeature(final String feature, final String role = null, final Long tenant = null) {
        if (crmFeatureService != null) {
            return crmFeatureService.hasFeature(feature, role, tenant)
        }
        return false
    }

    /**
     * Get informatin about a specific feature.
     *
     * @param feature name of feature
     * @return Map with feature properties
     */
    Map getFeature(final String feature) {
        if (crmFeatureService != null) {
            return crmFeatureService.getFeature(feature)
        }
        return null
    }

    /**
     * Check if an object is a domain instance reference (i.e. "domainClass@id").
     *
     * @param reference the string to analyze
     * @return true if the string matches the format "domainClass@id" and domainClass is a valid domain class.
     */
    boolean isDomainReference(final String reference) {
        if (!reference) {
            return false
        }
        final Matcher m = DOMAIN_REFERENCE_PATTERN.matcher(reference)
        m.matches() && grailsApplication.domainClasses.find { it.propertyName == m.group(1) }
    }

    /**
     * Check if an object is a domain instance.
     *
     * @param object the object to check
     * @return true if the object is a domain instance
     */
    boolean isDomainClass(final Object object) {
        grailsApplication.isDomainClass(GrailsHibernateUtil.unwrapIfProxy(object).getClass())
    }

    /**
     * Find a domain class in application context.
     *
     * @param name full class name of domain or it's property name i.e. "crmContact"
     */
    Class getDomainClass(final String name) {
        def domain = grailsApplication.domainClasses.find { it.propertyName == name }
        if (domain) {
            domain = domain.clazz
        } else {
            domain = grailsApplication.classLoader.loadClass(name)
        }
        return domain
    }

    /**
     * Get the reference identifier for a domain class instance.
     * A reference identifier is a string of form "domainClass@id".
     * For example "person@1234" is a reference to the Person domain instance with id 1234.
     *
     * @param object the domain instance
     * @return reference identifier "domainClass@id"
     */
    String getReferenceIdentifier(final Object object) {
        def ref
        if (object != null) {
            if (isDomainClass(object)) {
                def instance = GrailsHibernateUtil.unwrapIfProxy(object)
                ref = GrailsNameUtils.getPropertyName(instance.class) + '@' + instance.ident()
            } else {
                ref = object.toString()
            }
        }
        return ref
    }

    /**
     * Get the domain class instance by reference identifier.
     * @param identifier reference identifier (i.e. "domainClass@id")
     * @return the domain instance or null if not found
     */
    def getReference(final String identifier) {
        if (identifier) {
            def (name, key) = identifier.split('@').toList()
            if (name && key) {
                def domainClass = getDomainClass(name)
                if (domainClass) {
                    return domainClass.get(key)
                }
            }
        }
        return identifier
    }

    /**
     * Get the reference type for a domain class instance.
     * A reference type is the "property name" of a domain class.
     * For example "crmContact" is the reference type for the CrmContact domain.
     *
     * @param object the domain instance
     * @return reference type "domainClass"
     */
    String getReferenceType(final Object object) {
        def ref
        if (object != null) {
            if (isDomainClass(object)) {
                def instance = GrailsHibernateUtil.unwrapIfProxy(object)
                ref = GrailsNameUtils.getPropertyName(instance.class)
            } else {
                ref = GrailsNameUtils.getPropertyName(object)
            }
        }
        return ref
    }
}

