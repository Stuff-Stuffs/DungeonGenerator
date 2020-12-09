package io.github.stuff_stuffs.dungeon_generator.tree;

import java.util.Collection;

public interface MutableTree<V, E> extends Tree<V, E> {
    @Override
    MutableNode<V, E> getRoot();

    boolean insert(V parent, V child, E edge);

    boolean remove(V parent, V child);

    interface MutableNode<V, E> extends Node<V, E> {
        @Override
        MutableEdge<V, E> getParent();

        @Override
        Collection<MutableEdge<V, E>> getEdges();
    }

    interface MutableEdge<V, E> extends Edge<V, E> {

        default MutableNode<V, E> getOther(Node<V, E> current) {
            if (current == getParent()) {
                return getChild();
            }
            return getParent();
        }

        @Override
        MutableNode<V, E> getParent();

        @Override
        MutableNode<V, E> getChild();
    }
}
