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

import java.util.Iterator;
import java.util.Set;

public class RocksEdge extends TinkerElement implements Edge {
    private final Object edgeId;
    private final Object outVertexId;
    private final Object inVertexId;

    private final TinkerGraph graph;
    public RocksEdge(final Object id, final Object outVertexId, final String label, final Object inVertexId, final TinkerGraph graph) {
        super(id, label);
        this.edgeId = id;
        this.outVertexId = outVertexId;
        this.inVertexId = inVertexId;
        this.graph = graph;
    }
    @Override
    public <V> Property<V> property(String key, V value) {
        return null;
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public void remove() {
        this.graph.removeEdge(this.edgeId);
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    @Override
    public Object clone() {
        return new RocksEdge(this.edgeId, this.outVertexId, this.label(), this.inVertexId, this.graph);
    }

    @Override
    public Vertex outVertex() {
        return this.graph.vertex(this.outVertexId);
    }

    @Override
    public Vertex inVertex() {
        return this.graph.vertex(this.inVertexId);
    }

    @Override
    public Iterator<Vertex> vertices(final Direction direction) {
        return null;
    }

    @Override
    public Graph graph() {
        return this.graph;
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        return null;
    }
}
