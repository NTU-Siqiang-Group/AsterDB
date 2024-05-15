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

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.rocksdb.RocksDBException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class RocksVertices {
  private final RocksGraph db;

  // private final ColumnFamilyHandle adjacentListOut;

  // private final ColumnFamilyHandle adjacentListIn;

  // private final ColumnFamilyHandle vertexProperties;

  private final TinkerGraph graph;

  // public Map<Object, Vertex> inMemVertices = new ConcurrentHashMap<>();
  RocksVertices(RocksGraph db, final TinkerGraph graph) {
    this.db = db;
    this.graph = graph;
  }

  public Vertex addVertex(final Object vertexId) {
    long numId = Long.parseLong(String.valueOf(vertexId));
    try {
      // db.put(vertexProperties, String.valueOf(numId).getBytes(), "empty
      // placeholder".getBytes());
      db.AddVertex(numId);
    } catch (RocksDBException e) {
      e.printStackTrace();
    }
    return new RocksVertex(vertexId, this.graph, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
  }

  public Edge addEdge(final RocksVertex source, final RocksVertex target) {
    try {
      db.AddEdge(Long.parseLong(String.valueOf(source.id())), Long.parseLong(String.valueOf(target.id())));
    } catch (RocksDBException e) {
      e.printStackTrace();
    }
    return new RocksEdge(encodeEdgeId(source.id(), target.id()), source.id(), Edge.DEFAULT_LABEL, target.id(),
        this.graph);
  }

  void removeVertex(final Object vertexId) {
    long numId = Long.parseLong(vertexId.toString());
    // try {
    // // TODO: should invoke the graph interface directly
    // // db.delete(vertexProperties, String.valueOf(numId).getBytes());
    // // db.delete(adjacentListOut, String.valueOf(numId).getBytes());
    // // TODO: should remove all edges that point to this vertex (in edges)
    // } catch (RocksDBException e) {
    // e.printStackTrace();
    // }
  }

  void removeEdge(final Object edgeId) {
    // TODO: should invoke the graph interface directly
    Object outVertexId = getOutVertexIdFromEdgeId(edgeId);
    Object inVertexId = getInVertexIdFromEdgeId(edgeId);
    // TODO
    try {
      this.db.DeleteEdge(Long.parseLong(outVertexId.toString()), Long.parseLong(inVertexId.toString()));
    } catch (Exception e) {
      // do nothing
    }
  }

  public Boolean containsKey(final Object key) {
    // TODO: enable this function after we enable the ID manager
    // return db.keyMayExist(vertexProperties, String.valueOf(key).getBytes(),
    // null);
    return true;
  }

  public long vertexNum() {
    try {
      return db.CountVertex();
    } catch (RocksDBException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public long edgeNum() {
    try {
      return db.CountEdge();
    } catch (RocksDBException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public void clear() {
    // TODO: should replace with graph interface
    // do nothing
  }

  public Edge getEdge(final Object edgeId) {
    return null;
  }

  public Vertex getVertex(final Object key) {
    // Get the properties and its in/out edges
    // String edgeStr = "";
    // try {
    // db.get(adjacentListOut, String.valueOf(key).getBytes(), edgeStr.getBytes());
    // Set<Edge> outEdges = decodeEdges(edgeStr, key, Direction.OUT);
    // Map<String, Set<Edge>> outEdgesMap = new ConcurrentHashMap<>();
    // outEdgesMap.put(Edge.DEFAULT_LABEL, outEdges);
    // db.get(adjacentListIn, String.valueOf(key).getBytes(), edgeStr.getBytes());
    // Set<Edge> inEdges = decodeEdges(edgeStr, key, Direction.IN);
    // Map<String, Set<Edge>> inEdgesMap = new ConcurrentHashMap<>();
    // inEdgesMap.put(Edge.DEFAULT_LABEL, inEdges);
    // return new RocksVertex(key, this.graph, outEdgesMap, inEdgesMap);
    // } catch (RocksDBException e) {
    // e.printStackTrace();
    // }
    return new RocksVertex(key, this.graph);
  }

  private Set<Edge> decodeEdges(final String edgeListStr, Object vertexId, Direction direction) {
    Set<Edge> edgeList = new HashSet<>();
    String[] edgeStrList = edgeListStr.split(",");
    for (String edgeStr : edgeStrList) {
      Object edgeId;
      if (direction.equals(Direction.OUT)) {
        edgeId = vertexId.toString() + "-" + edgeStr;
      } else {
        edgeId = edgeStr + "-" + vertexId.toString();
      }
      RocksEdge edge = new RocksEdge(edgeId, vertexId, "edgeLabel", edgeStr, this.graph);
      edgeList.add(edge);
    }
    return edgeList;
  }

  private List<Object> decodeEdgeId(final Object edgeId) {
    String edgeIdStr = edgeId.toString();
    String[] vertexIds = edgeIdStr.split("-");
    List<Object> vertexIdList = new ArrayList<>();
    vertexIdList.add(vertexIds[0]);
    vertexIdList.add(vertexIds[1]);
    return vertexIdList;
  }

  private Object getOutVertexIdFromEdgeId(final Object edgeId) {
    return decodeEdgeId(edgeId).get(0);
  }

  private Object getInVertexIdFromEdgeId(final Object edgeId) {
    return decodeEdgeId(edgeId).get(1);
  }

  private Object encodeEdgeId(final Object outVertexId, final Object inVertexId) {
    return outVertexId.toString() + "-" + inVertexId.toString();
  }
}
