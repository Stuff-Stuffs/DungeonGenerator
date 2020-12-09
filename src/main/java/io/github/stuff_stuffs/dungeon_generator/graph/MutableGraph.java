package io.github.stuff_stuffs.dungeon_generator.graph;

import io.github.stuff_stuffs.dungeon_generator.tree.Tree;

import java.util.function.Supplier;

public interface MutableGraph<V, E> extends Graph<V, E> {
    boolean addEdge(V first, V second, E edgeValue, boolean replace);

    boolean insert(V value);

    boolean remove(V value);

    @Override
    MutableGraph<V, E> copy();

    static <G extends MutableGraph<V, E>, V, E> G fromTree(final Tree<V, E> tree, final Supplier<G> graphSupplier) {
        final G graph = graphSupplier.get();
        recurse(tree.getRoot(), graph);
        return graph;
    }

    static <G extends MutableGraph<V, E>, V, E> void recurse(final Tree.Node<V, E> node, final G graph) {
        graph.insert(node.getValue());
        for (final Tree.Edge<V, E> edge : node.getEdges()) {
            final Tree.Node<V, E> other = edge.getOther(node);
            recurse(other, graph);
            graph.addEdge(node.getValue(), other.getValue(), edge.getValue(), false);
        }
    }
}
