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

final class RocksIndexVertex<T extends Element> extends AbstractTinkerIndex<T> {

    protected Map<String, Map<Object, Set<T>>> index = new ConcurrentHashMap<>();
    private final RocksGraph db;

    public RocksIndexVertex(final TinkerGraph graph, final Class<T> indexClass, RocksGraph db) {
        super(graph, indexClass);
        this.db = db;
    }

    public void attachDB(RocksGraph db) {
        this.db = db;
    }

    protected void put(final String key, final Object value, final T element) {
        
    }

    @Override
    public List<T> get(final String key, final Object value) {
        long[] vertexArray = db.GetVertexWithProperty(key, String.valueOf(value));
        Set<RocksVertex> vertexlist = new HashSet<>();
        for (int i = 0; i < vertexArray.length; i += 1) {
            long source = vertexArray[i];
            RocksVertex edge = RocksVertex(source, this.graph, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            vertexlist.add(edge);
        }
        return new ArrayList<>(vertexArray);
    }

    @Override
    public long count(final String key, final Object value) {
        return 0;
    }

    @Override
    public void remove(final String key, final Object value, final T element) {
        
    }

    @Override
    public void removeElement(final T element) {
        
    }

    @Override
    public void autoUpdate(final String key, final Object newValue, final Object oldValue, final T element) {
        
    }

    @Override
    public void createKeyIndex(final String key) {
        
    }

    @Override
    public void dropKeyIndex(final String key) {
        
    }
}
