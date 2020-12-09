package io.github.stuff_stuffs.dungeon_generator.tree;

import java.util.Collection;

public interface Tree<V, E> {
    Node<V, E> getRoot();

    interface Node<V, E> {
        V getValue();

        Edge<V, E> getParent();

        Collection<? extends Edge<V, E>> getEdges();
    }

    interface Edge<V, E> {
        E getValue();

        default Node<V, E> getOther(final Node<V, E> current) {
            if (current == getParent()) {
                return getChild();
            }
            return getParent();
        }

        Node<V, E> getParent();

        Node<V, E> getChild();
    }
}
