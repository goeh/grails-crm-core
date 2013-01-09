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

/**
 * A simple directed graph.
 */
class Graph {
    List<Vertex> vertexes = []
    List<Edge> edges = []

    Vertex getVertex(Object arg) {
        vertexes.find { it.obj == arg }
    }

    Vertex addVertex(Object arg) {
        def v = getVertex(arg)
        if (!v) {
            v = new Vertex(arg)
            vertexes << v
        }
        return v
    }

    Edge addEdge(Object from, Object to) {
        def v1 = getVertex(from) ?: addVertex(from)
        def v2 = getVertex(to) ?: addVertex(to)
        def edge = edges.find { it.source == v1 && it.target == v2 }
        if (!edge) {
            edge = new Edge(v1, v2)
            edges << edge
        }
        return edge
    }

    Edge addEdge(Object from, Object to, double weight) {
        def v1 = getVertex(from) ?: addVertex(from)
        def v2 = getVertex(to) ?: addVertex(to)
        def edge = edges.find { it.source == v1 && it.target == v2 }
        if (edge) {
            edge.weight = weight
        } else {
            edge = new Edge(v1, v2, weight)
            edges << edge
        }
        return edge
    }

    List<Vertex> getSources(Vertex target) {
        edges.findAll { it.target == target }.collect { it.source }
    }

    List<Vertex> getTargets(Vertex source) {
        edges.findAll { it.source == source }.collect { it.target }
    }

    Iterator<Vertex> iterator() {
        def result = []
        def workList = vertexes.clone()
        def edgeList = edges.clone()
        int i = 0
        while (workList) {
            def leafs = findLeafs(workList, edgeList)
            for (v in leafs) {
                result << v
                workList.remove(v)
                edgeList.removeAll { it.target == v }
            }
            if (i++ > 50) {
                throw new RuntimeException("Graph is cyclic or too complex")
            }
        }
        result.reverse().iterator()
    }

    private List<Vertex> findLeafs(List<Vertex> vList, List<Edge> eList) {
        vList.findAll { v -> !eList.find { v == it.source } }
    }

    String toString() {
        def orphans = []
        orphans.addAll(vertexes)
        def s = []
        for (edge in edges) {
            s << edge.toString()
            orphans.remove(edge.source)
            orphans.remove(edge.target)
        }
        for (v in orphans) {
            s << v.toString()
        }
        s.join(', ')
    }

}
