/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.tinkergraph.structure;

import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RocksVertex extends TinkerElement implements Vertex {
    final Object vertexId;
    final TinkerGraph graph;
    // label->EdgeIdList
    Map<String, Set<Edge>> outEdgesId;
    Map<String, Set<Edge>> inEdgesId;
    public Boolean outEdgeFetched = false;
    public Boolean inEdgeFetched = false;
    public Boolean isRemoved = false;

    public RocksVertex(final Object id, final TinkerGraph graph) {
        super(id, Vertex.DEFAULT_LABEL);
        this.vertexId = id;
        this.graph = graph;
        this.outEdgeFetched = false;
        this.inEdgeFetched = false;
    }

    public RocksVertex(final Object id, final TinkerGraph graph, final Map<String, Set<Edge>> outEdgesId,
            final Map<String, Set<Edge>> inEdgesId) {
        super(id, Vertex.DEFAULT_LABEL);
        this.vertexId = id;
        this.graph = graph;
        this.outEdgesId = outEdgesId;
        this.inEdgesId = inEdgesId;
        this.outEdgeFetched = false;
        this.inEdgeFetched = false;
    }


    public RocksVertex(final Object id, final TinkerGraph graph, final Map<String, Set<Edge>> outEdgesId,
            final Map<String, Set<Edge>> inEdgesId, final Boolean outEdgeFetched, final Boolean inEdgeFetched) {
        super(id, Vertex.DEFAULT_LABEL);
        this.vertexId = id;
        this.graph = graph;
        this.outEdgesId = outEdgesId;
        this.inEdgesId = inEdgesId;
        this.outEdgeFetched = outEdgeFetched;
        this.inEdgeFetched = inEdgeFetched;
    }


    @Override
    public <V> VertexProperty<V> property(final String key) {
        return null;
    }

    @Override
    public <V> VertexProperty<V> property(final VertexProperty.Cardinality cardinality, final String key, final V value,
            final Object... keyValues) {
        graph.addVertexProperty(vertexId, key, value);
        final Property<V> newProperty = new TinkerProperty<>(this, key, value);
        //if (null == this.properties) this.properties = new ConcurrentHashMap<>();
        //this.properties.put(key, newProperty);
        return newProperty;
    }

    @Override
    public Object clone() {
        return new RocksVertex(this.vertexId, this.graph, this.outEdgesId, this.inEdgesId, this.outEdgeFetched, this.inEdgeFetched);
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public Edge addEdge(final String label, final Vertex dstVertex, final Object... keyValues) {
        if (null == dstVertex) {
            throw Graph.Exceptions.argumentCanNotBeNull("dstVertex");
        }
        if (isRemoved || ((RocksVertex) dstVertex).isRemoved) {
            throw elementAlreadyRemoved(Vertex.class, this.id);
        }
        return graph.addEdge(this, (RocksVertex) dstVertex, label, keyValues);
    }

    @Override
    public Graph graph() {
        return this.graph;
    }

    @Override
    public void remove() {
        // TODO: invoke the graph interface directly
        isRemoved = true;
        graph.removeVertex(this.vertexId);
    }

    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }

    @Override
    public Iterator<Edge> edges(final Direction direction, final String... edgeLabels) {
        final List<Edge> edgeList = new ArrayList<>();
        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            if (!outEdgeFetched) {
                //Set<Edge> outEdges = graph.GetOutNeighbours(vertexId);
                outEdgesId = new HashMap<>();
                outEdgesId.put(Edge.DEFAULT_LABEL, graph.GetOutNeighbours(vertexId));
                outEdgeFetched = true;
            }
            // for (final String edgeLabel : edgeLabels) {
            //     final Set<Edge> edgeSet = outEdgesId.get(edgeLabel);
            //     if (null != edgeSet) {
            //         edgeList.addAll(edgeSet);
            //     }
            // }
            final String edgeLabel = Edge.DEFAULT_LABEL;
            final Set<Edge> edgeSet = outEdgesId.get(edgeLabel);
            if (null != edgeSet) {
                edgeList.addAll(edgeSet);
            }
        }
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            if (!inEdgeFetched) {
                inEdgesId = new HashMap<>();
                inEdgesId.put(Edge.DEFAULT_LABEL, graph.GetInNeighbours(vertexId));
                inEdgeFetched = true;
            }
            // for (final String edgeLabel : edgeLabels) {
            //     final Set<Edge> edgeSet = inEdgesId.get(edgeLabel);
            //     if (null != edgeSet) {
            //         edgeList.addAll(edgeSet);
            //     }
            // }
            final String edgeLabel = Edge.DEFAULT_LABEL;
            final Set<Edge> edgeSet = outEdgesId.get(edgeLabel);
            if (null != edgeSet) {
                edgeList.addAll(edgeSet);
            }
        }
        return TinkerHelper.inComputerMode(this.graph)
                ? IteratorUtils.filter(edgeList.iterator(), e -> this.graph.graphComputerView.legalEdge(this, e))
                : edgeList.iterator();
    }

    @Override
    public Iterator<Vertex> vertices(final Direction direction, final String... edgeLabels) {
        return direction.equals(Direction.BOTH) ? IteratorUtils.concat(
                IteratorUtils.map(this.edges(Direction.OUT, edgeLabels), Edge::inVertex),
                IteratorUtils.map(this.edges(Direction.IN, edgeLabels), Edge::outVertex))
                : IteratorUtils.map(this.edges(direction, edgeLabels),
                        direction.equals(Direction.OUT) ? Edge::inVertex : Edge::outVertex);
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(final String... propertyKeys) {
        return null;
    }
}
