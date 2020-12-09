package io.github.stuff_stuffs.dungeon_generator.graph;

import java.util.List;

public interface Graph<V, E> extends Iterable<V> {
    Node<V, E> get(V value);

    Edge<V, E> getEdge(V first, V second);

    Iterable<Edge<V, E>> getEdges();

    Graph<V, E> copy();

    int size();

    interface Node<V, E> {
        List<? extends Edge<V, E>> getEdges();

        V getValue();
    }

    interface Edge<V, E> {
        Node<V, E> getFirst();

        Node<V, E> getSecond();

        default Node<V, E> getOther(final Node<V, E> current) {
            if (current == getFirst()) {
                return getSecond();
            }
            return getFirst();
        }

        E getValue();
    }
}
