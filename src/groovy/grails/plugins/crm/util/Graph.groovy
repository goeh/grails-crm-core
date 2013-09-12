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
 * A simple directed graph.
 */
@CompileStatic
class Graph {
    private final List<Vertex> vertexes = []
    private final List<Edge> edges = []

    Vertex getVertex(final Object arg) {
        vertexes.find { Vertex v -> v.obj == arg }
    }

    Vertex addVertex(final Object arg) {
        Vertex v = getVertex(arg)
        if (!v) {
            v = new Vertex(arg)
            vertexes << v
        }
        return v
    }

    Edge addEdge(Object from, Object to) {
        final Vertex v1 = getVertex(from) ?: addVertex(from)
        final Vertex v2 = getVertex(to) ?: addVertex(to)
        Edge edge = edges.find { Edge e -> e.source == v1 && e.target == v2 }
        if (!edge) {
            edge = new Edge(v1, v2)
            edges << edge
        }
        return edge
    }

    Edge addEdge(Object from, Object to, double weight) {
        final Vertex v1 = getVertex(from) ?: addVertex(from)
        final Vertex v2 = getVertex(to) ?: addVertex(to)
        Edge edge = edges.find { Edge e -> e.source == v1 && e.target == v2 }
        if (edge) {
            edge.weight = weight
        } else {
            edge = new Edge(v1, v2, weight)
            edges << edge
        }
        return edge
    }

    List<Vertex> getSources(Vertex target) {
        edges.findAll { Edge e -> e.target == target }.collect { Edge e -> e.source }
    }

    List<Vertex> getTargets(Vertex source) {
        edges.findAll { Edge e -> e.source == source }.collect { Edge e -> e.target }
    }

    Iterator<Vertex> iterator() {
        final List<Vertex> result = []
        final List<Vertex> workList = []
        workList.addAll(vertexes)
        final List<Edge> edgeList = []
        edgeList.addAll(edges)
        int i = 0
        while (workList) {
            def leafs = findLeafs(workList, edgeList)
            for (v in leafs) {
                result << v
                workList.remove(v)
                edgeList.removeAll { Edge e -> e.target == v }
            }
            if (i++ > 50) {
                throw new RuntimeException("Graph is cyclic or too complex")
            }
        }
        result.reverse().iterator()
    }

    private List<Vertex> findLeafs(final List<Vertex> vList, final List<Edge> eList) {
        vList.findAll { Vertex v -> !eList.find { Edge e -> v == e.source } }
    }

    String toString() {
        final List<Vertex> orphans = []
        orphans.addAll(vertexes)
        final List<String> s = []
        for (edge in edges) {
            s << edge.toString()
            orphans.remove(edge.source)
            orphans.remove(edge.target)
        }
        for (Vertex v in orphans) {
            s << v.toString()
        }
        return s.join(', ')
    }

}
