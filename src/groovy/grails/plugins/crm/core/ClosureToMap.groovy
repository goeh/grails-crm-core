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
 * Utility class to convert Groovy closures to Map.
 */
class ClosureToMap {
    Map props

    static Map convert(Closure c) {
        def map = [:]
        new ClosureToMap(map, c)
        return map
    }

    ClosureToMap(Map map, Closure c) {
        iterate(map, c)
    }

    def iterate(Map map, Closure c) {
        def prev = props
        props = map
        c.delegate = this
        c.each {"$it"()}
        props = prev
    }

    def methodMissing(String name, args) {
        if (!args.size()) return

        // nested closure, recurse
        if (args[0] in Closure) {
            def map = props[name] = [:]
            iterate(map, args[0])
        } else {
            props[name] = (args.size() > 1 ? args.toList() : args[0])
        }
    }

    def propertyMissing(String name) {
        name
    }
}