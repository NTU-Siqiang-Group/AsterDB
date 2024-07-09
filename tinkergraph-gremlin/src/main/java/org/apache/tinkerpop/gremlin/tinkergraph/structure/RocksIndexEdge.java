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
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksGraph;
import org.rocksdb.RocksDBException;


import java.util.Iterator;
import java.util.Set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class RocksIndexEdge extends AbstractTinkerIndex<RocksEdge>{

    //protected Map<String, Map<Object, Set<T>>> index = new ConcurrentHashMap<>();
    private final RocksGraph db;
    private final TinkerGraph tinkerGraph;

    public RocksIndexEdge(final TinkerGraph graph, final Class<RocksEdge> indexClass, RocksGraph db) {
        super(graph, indexClass);
        this.db = db;
        this.tinkerGraph = graph;
    }

    protected void put(final String key, final Object value, final RocksEdge element) {
        
    }

    @Override
    public List<RocksEdge> get(final String key, final Object value) {
        try{
        System.out.println(key);
        System.out.println(value);
        long[] edgeArray = db.GetEdgeWithProperty(key, String.valueOf(value));
        Set<RocksEdge> edgeList = new HashSet<>();
        for (int i = 0; i < edgeArray.length; i += 2) {
            Object source = edgeArray[i];
            Object target = edgeArray[i + 1];
            Object edgeId;
            edgeId = target.toString() + "-" + source.toString();
            RocksEdge edge = new RocksEdge(edgeId, source, "edgeLabel", target, this.tinkerGraph);
            final Property<Object> property =  new TinkerProperty<Object>(edge, key, value);
            edge.propertyMap.put(key, property);
            edgeList.add(edge);
        }
        return new ArrayList<>(edgeList);
        } catch (RocksDBException e) {
        e.printStackTrace();
        }
        return null;
    }

    @Override
    public long count(final String key, final Object value) {
        return 0;
    }

    @Override
    public void remove(final String key, final Object value, final RocksEdge element) {
        
    }

    @Override
    public void removeElement(final RocksEdge element) {
        
    }

    @Override
    public void autoUpdate(final String key, final Object newValue, final Object oldValue, final RocksEdge element) {
        
    }

    @Override
    public void createKeyIndex(final String key) {
        this.indexedKeys.add(key);
    }

    @Override
    public void dropKeyIndex(final String key) {
        
    }
}
