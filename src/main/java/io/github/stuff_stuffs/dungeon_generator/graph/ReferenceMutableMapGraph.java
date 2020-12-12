package io.github.stuff_stuffs.dungeon_generator.graph;

import it.unimi.dsi.fastutil.objects.*;

public class ReferenceMutableMapGraph<V, E> extends AbstractMutableMapGraph<V, E> {
    public ReferenceMutableMapGraph() {
        super(new Reference2ReferenceLinkedOpenHashMap<>(), new Object2ObjectLinkedOpenHashMap<>(), ObjectArrayList::new);
    }

    public ReferenceMutableMapGraph(final Graph<V, E> graph) {
        super(new Reference2ReferenceOpenHashMap<>(graph.size()), new Reference2ReferenceOpenHashMap<>(graph.size()), ReferenceArrayList::new);
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
    public ReferenceMutableMapGraph<V, E> copy() {
        final ReferenceMutableMapGraph<V, E> graph = new ReferenceMutableMapGraph<>();
        for (final V v : this) {
            graph.insert(v);
        }
        for (final Edge<V, E> edge : getEdges()) {
            graph.addEdge(edge.getFirst().getValue(), edge.getSecond().getValue(), edge.getValue(), false);
        }
        return graph;
    }
}
