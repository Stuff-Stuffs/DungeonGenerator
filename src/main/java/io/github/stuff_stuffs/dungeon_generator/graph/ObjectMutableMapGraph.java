package io.github.stuff_stuffs.dungeon_generator.graph;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ObjectMutableMapGraph<V, E> extends AbstractMutableMapGraph<V, E> {
    public ObjectMutableMapGraph() {
        super(new Object2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>(), ObjectArrayList::new);
    }

    public ObjectMutableMapGraph(final Graph<V, E> graph) {
        super(new Object2ObjectOpenHashMap<>(graph.size()), new Object2ObjectOpenHashMap<>(graph.size()), ObjectArrayList::new);
        for (final V v : graph) {
            insert(v);
        }
        for (final Edge<V, E> edge : graph.getEdges()) {
            if (!addEdge(edge.getFirst().getValue(), edge.getSecond().getValue(), edge.getValue(), false)) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public ObjectMutableMapGraph<V, E> copy() {
        final ObjectMutableMapGraph<V, E> graph = new ObjectMutableMapGraph<>();
        for (final V v : this) {
            graph.insert(v);
        }
        for (final Edge<V, E> edge : getEdges()) {
            graph.addEdge(edge.getFirst().getValue(), edge.getSecond().getValue(), edge.getValue(), false);
        }
        return graph;
    }
}
