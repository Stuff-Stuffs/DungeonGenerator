package io.github.stuff_stuffs.dungeon_generator.graph;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractMutableMapGraph<V, E> implements MutableGraph<V, E> {
    private final Map<V, Node<V, E>> nodeMap;
    private final Map<Node<V, E>, List<Edge<V, E>>> edges;
    private final Collection<Edge<V, E>> edgeCollection;
    private final Supplier<? extends List<Edge<V, E>>> collectionFactory;

    protected AbstractMutableMapGraph(final Map<V, Node<V, E>> nodeMap, final Map<Node<V, E>, List<Edge<V, E>>> edges, final Supplier<? extends List<Edge<V, E>>> collectionFactory) {
        this.nodeMap = nodeMap;
        this.edges = edges;
        this.collectionFactory = collectionFactory;
        edgeCollection = collectionFactory.get();
    }

    @Override
    public boolean removeEdge(final V first, final V second) {
        final EdgeImpl edge = (EdgeImpl) getEdge(first, second);
        if (edge == null) {
            return false;
        }
        edges.get(edge.first).remove(edge);
        edges.get(edge.second).remove(edge);
        edgeCollection.remove(edge);
        return true;
    }

    @Override
    public int size() {
        return nodeMap.size();
    }

    @Override
    public Iterable<Edge<V, E>> getEdges() {
        return edgeCollection;
    }

    @Override
    public Iterator<V> iterator() {
        return nodeMap.keySet().iterator();
    }

    @Override
    public Node<V, E> get(final V value) {
        return nodeMap.get(value);
    }

    @Override
    public boolean addEdge(final V first, final V second, final E edgeValue, final boolean replace) {
        final Node<V, E> firstNode = nodeMap.get(first);
        if (firstNode == null) {
            return false;
        }
        final Node<V, E> secondNode = nodeMap.get(second);
        if (secondNode == null) {
            return false;
        }
        final Edge<V, E> edge = getEdge(first, second);
        if (edge != null && !replace) {
            return false;
        }
        if (edge != null) {
            nodeMap.get(first).getEdges().remove(edge);
            nodeMap.get(second).getEdges().remove(edge);
            edgeCollection.remove(edge);
        }
        final Edge<V, E> targetEdge = new EdgeImpl(edgeValue, firstNode, secondNode);
        nodeMap.get(first).getEdges().add(targetEdge);
        nodeMap.get(second).getEdges().add(targetEdge);
        edgeCollection.add(targetEdge);
        return true;
    }

    @Override
    public boolean insert(final V value) {
        Node<V, E> node = nodeMap.get(value);
        if (node == null) {
            node = new NodeImpl(value);
            nodeMap.put(value, node);
            edges.put(node, collectionFactory.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(final V value) {
        final Node<V, E> node = nodeMap.remove(value);
        if (node != null) {
            final Collection<Edge<V, E>> edges = this.edges.remove(node);
            for (final Edge<V, E> edge : edges) {
                final Node<V, E> first = edge.getFirst();
                final Node<V, E> second = edge.getSecond();
                edgeCollection.remove(edge);
                if (node == first) {
                    this.edges.get(second).remove(edge);
                } else {
                    this.edges.get(first).remove(edge);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Edge<V, E> getEdge(final V first, final V second) {
        final Node<V, E> firstNode = nodeMap.get(first);
        if (firstNode == null) {
            return null;
        }
        final Node<V, E> secondNode = nodeMap.get(second);
        if (secondNode == null) {
            return null;
        }
        final Collection<Edge<V, E>> firstEdges = edges.get(firstNode);
        for (final Edge<V, E> edge : firstEdges) {
            if (edge.getOther(firstNode) == secondNode) {
                return edge;
            }
        }
        return null;
    }

    private class NodeImpl implements Node<V, E> {
        private final V value;

        private NodeImpl(final V value) {
            this.value = value;
        }

        @Override
        public List<Edge<V, E>> getEdges() {
            return edges.get(this);
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final NodeImpl node = (NodeImpl) o;

            return value.equals(node.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    private class EdgeImpl implements Edge<V, E> {
        private final E value;
        private final Node<V, E> first;
        private final Node<V, E> second;

        private EdgeImpl(final E value, final Node<V, E> first, final Node<V, E> second) {
            this.value = value;
            this.first = first;
            this.second = second;
        }

        @Override
        public Node<V, E> getFirst() {
            return first;
        }

        @Override
        public Node<V, E> getSecond() {
            return second;
        }

        @Override
        public E getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final EdgeImpl edge = (EdgeImpl) o;

            if (!value.equals(edge.value)) {
                return false;
            }
            return (first.equals(edge.first) && second.equals(edge.second)) || first.equals(edge.second) && second.equals(edge.first);
        }

        @Override
        public int hashCode() {
            int result = value.hashCode();
            result = 31 * result + first.hashCode();
            result = result + second.hashCode();
            return result;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractMutableMapGraph<?, ?> that = (AbstractMutableMapGraph<?, ?>) o;

        if (!nodeMap.equals(that.nodeMap)) {
            return false;
        }
        return edges.equals(that.edges);
    }

    @Override
    public int hashCode() {
        int result = nodeMap.hashCode();
        result = 31 * result + edges.hashCode();
        return result;
    }
}
