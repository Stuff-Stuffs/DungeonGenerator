package io.github.stuff_stuffs.dungeon_generator.graph;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class ReferenceMutableMapGraph<V, E> extends AbstractMutableMapGraph<V, E> {
    public ReferenceMutableMapGraph() {
        super(new Reference2ReferenceOpenHashMap<>(), new Reference2ReferenceOpenHashMap<>(), ReferenceArrayList::new);
    }

    public ReferenceMutableMapGraph(Graph<V, E> graph) {
        super(new Reference2ReferenceOpenHashMap<>(graph.size()), new Reference2ReferenceOpenHashMap<>(graph.size()), ReferenceArrayList::new);
        for (V v : graph) {
            insert(v);
        }
        for (Edge<V, E> edge : graph.getEdges()) {
            if(!addEdge(edge.getFirst().getValue(), edge.getSecond().getValue(), edge.getValue(), false)) {
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
