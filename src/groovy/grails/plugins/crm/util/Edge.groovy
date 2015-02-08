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



package grails.plugins.crm.util

import groovy.transform.CompileStatic

/**
 * A simple edge.
 */
@CompileStatic
class Edge {
    private final Vertex source
    private final Vertex target
    Double weight // Weight is mutable but source and target are not

    private Edge() {}

    Edge(Vertex source, Vertex target) {
        this.source = source
        this.target = target
        this.weight = 0
    }

    Edge(Vertex source, Vertex target, double weight) {
        this.source = source
        this.target = target
        this.weight = weight
    }

    Vertex getSource() {
        source
    }

    Vertex getTarget() {
        target
    }

    String toString() {
        source.toString() + '-(' + weight + ')->' + target.toString()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Edge edge = (Edge) o

        if (source != edge.source) return false
        if (target != edge.target) return false
        if (weight != edge.weight) return false

        return true
    }

    int hashCode() {
        int result
        result = (source != null ? source.hashCode() : 0)
        result = 31 * result + (target != null ? target.hashCode() : 0)
        result = 31 * result + weight.intValue()
        return result
    }
}

