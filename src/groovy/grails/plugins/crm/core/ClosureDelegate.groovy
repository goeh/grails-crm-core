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

/**
 * Convenience class for use with Groovy closures.
 */
class ClosureDelegate {
    def methodDelegate
    def props
    def grailsApplication
    def model

    ClosureDelegate(md, app, m, Map concreteProps) {
        methodDelegate = md
        props = concreteProps
        grailsApplication = app
        model = m
    }

    /**
     * Return a predefined property or bean from the context
     */
    def propertyMissing(String name) {
        if (this.@props.containsKey(name)) {
            return this.@props[name]
        } else if (this.@model.containsKey(name)) {
            return this.@model[name]
        } else {
            return this.@grailsApplication.mainContext.getBean(name)
        }
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
