/*
 * Copyright (c) 2014 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.core

import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

/**
 * Convenience class for use with Groovy closures.
 */
class ClosureDelegate {
    final GrailsApplication grailsApplication
    final ApplicationContext applicationContext
    final Object methodDelegate
    final Map props
    final Map model

    @CompileStatic
    ClosureDelegate(Object delegate, GrailsApplication app, Map model, Map concreteProps) {
        this.methodDelegate = delegate
        this.props = concreteProps
        this.model = model
        this.grailsApplication = app
        if(app != null) {
            this.applicationContext = app.mainContext
        }
    }

    /**
     * Clients can use both 'application' and 'grailsApplication' to get the same instance.
     * @return the GrailsApplication instance set in the constructor.
     */
    @CompileStatic
    GrailsApplication getApplication() {
        grailsApplication
    }

    /**
     * Return a predefined property or bean from the context
     */
    def propertyMissing(String name) {
        if (this.@props.containsKey(name)) {
            return this.@props[name]
        } else if (this.@model.containsKey(name)) {
            return this.@model[name]
        } else if(this.@applicationContext.containsBean(name)) {
            return this.@applicationContext.getBean(name)
        }
        return null
    }

    def methodMissing(String name, args) {
        if (args == null || args.size() == 0) {
            methodDelegate."$name"()
        } else if (args.size() == 1) {
            methodDelegate."$name"(args[0])
        } else {
            methodDelegate."$name"(* args)
        }
    }
}
