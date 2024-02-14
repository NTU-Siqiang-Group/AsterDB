package org.apache.tinkerpop.gremlin.tinkergraph.structure;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
public class RocksVertex extends TinkerElement implements Vertex {
    final Object vertexId;
    final TinkerGraph graph;
    // label->EdgeIdList
    final Map<String, Set<Edge>> outEdgesId;
    final Map<String, Set<Edge>> inEdgesId;
    public Boolean isRemoved = false;

    public RocksVertex(final Object id, final TinkerGraph graph, final Map<String, Set<Edge>> outEdgesId, final Map<String, Set<Edge>> inEdgesId) {
        super(id, Vertex.DEFAULT_LABEL);
        this.vertexId = id;
        this.graph = graph;
        this.outEdgesId = outEdgesId;
        this.inEdgesId = inEdgesId;
    }
    @Override
    public <V> VertexProperty<V> property(final String key) {
        return null;
    }

    @Override
    public <V> VertexProperty<V> property(final VertexProperty.Cardinality cardinality, final String key, final V value, final Object... keyValues) {
        return null;
    }
    @Override
    public Object clone() {
        return new RocksVertex(this.vertexId, this.graph, this.outEdgesId, this.inEdgesId);
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
        if (isRemoved || ((RocksVertex)dstVertex).isRemoved) {
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
        final List<Edge> edges = new ArrayList<>();
        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            for (final String edgeLabel : edgeLabels) {
                final Set<Edge> edgeSet = outEdgesId.get(edgeLabel);
                if (null != edgeSet) {
                    edges.addAll(edgeSet);
                }
            }
        }
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            for (final String edgeLabel : edgeLabels) {
                final Set<Edge> edgeSet = inEdgesId.get(edgeLabel);
                if (null != edgeSet) {
                    edges.addAll(edgeSet);
                }
            }
        }
        return TinkerHelper.inComputerMode(this.graph) ?
                IteratorUtils.filter(edges.iterator(), e -> this.graph.graphComputerView.legalEdge(this, e)) :
                edges.iterator();
    }

    @Override
    public Iterator<Vertex> vertices(final Direction direction, final String... edgeLabels) {
        return direction.equals(Direction.BOTH) ?
                IteratorUtils.concat(
                        IteratorUtils.map(this.edges(Direction.OUT, edgeLabels), Edge::inVertex),
                        IteratorUtils.map(this.edges(Direction.IN, edgeLabels), Edge::outVertex)
                ) :
                IteratorUtils.map(this.edges(direction, edgeLabels), direction.equals(Direction.OUT) ? Edge::inVertex : Edge::outVertex);
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(final String... propertyKeys) {
        return null;
    }
}
