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



package grails.plugins.crm.util

import groovy.transform.CompileStatic

/**
 * A simple vertex.
 */
@CompileStatic
final class Vertex {
    private final Object obj

    Vertex(Object arg) {
        if(arg == null) {
            throw new IllegalArgumentException("A null argument was sent to Vertex constructor")
        }
        this.obj = arg
    }

    Object getObj() {
        obj
    }

    String toString() {
        obj.toString()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Vertex vertex = (Vertex) o

        if (obj != vertex.obj) return false

        return true
    }

    int hashCode() {
        return (obj != null ? obj.hashCode() : 0)
    }
}
