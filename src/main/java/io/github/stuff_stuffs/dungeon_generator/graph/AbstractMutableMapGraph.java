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
        return edgeCollection::iterator;
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
        final Edge<V, E> targetEdge = new EdgeImpl(edgeValue, firstNode, secondNode);
        final Collection<Edge<V, E>> firstEdges = edges.get(firstNode);
        boolean firstValid = true;
        for (final Iterator<Edge<V, E>> iterator = firstEdges.iterator(); iterator.hasNext(); ) {
            final Edge<V, E> edge = iterator.next();
            final Node<V, E> edgeFirst = edge.getFirst();
            final Node<V, E> edgeSecond = edge.getSecond();
            if (edgeFirst == firstNode) {
                if (edgeSecond == secondNode) {
                    if (replace) {
                        iterator.remove();
                    } else {
                        firstValid = false;
                    }
                }
            }
        }
        if (!firstValid) {
            return false;
        }
        boolean secondValid = true;
        final Collection<Edge<V, E>> secondEdges = edges.get(secondNode);
        for (final Iterator<Edge<V, E>> iterator = secondEdges.iterator(); iterator.hasNext(); ) {
            final Edge<V, E> edge = iterator.next();
            final Node<V, E> edgeFirst = edge.getFirst();
            final Node<V, E> edgeSecond = edge.getSecond();
            if (edgeFirst == firstNode) {
                if (edgeSecond == secondNode) {
                    if (replace) {
                        iterator.remove();
                    } else {
                        secondValid = false;
                    }
                }
            }
        }
        if (firstValid & secondValid) {
            firstEdges.add(targetEdge);
            secondEdges.add(targetEdge);
            edgeCollection.add(targetEdge);
            return true;
        }
        return false;
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
        public List<? extends Edge<V, E>> getEdges() {
            return Collections.unmodifiableList(edges.get(this));
        }

        @Override
        public V getValue() {
            return value;
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
    }
}
