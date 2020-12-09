package io.github.stuff_stuffs.dungeon_generator.graph;

import io.github.stuff_stuffs.dungeon_generator.util.RandomUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.*;

public final class RandomPartition {
    public static <V, E> Map<Graph.Node<V, E>, Partition<V, E>> partition(final Graph<V, E> graph, final int partitionCount, final Random random) {
        if (partitionCount > graph.size()) {
            throw new RuntimeException();
        }
        final Set<V> visited = new ReferenceOpenHashSet<>();
        final List<List<V>> partitions = new ObjectArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            while (true) {
                final V node = RandomUtil.getRandom(graph, graph.size(), random);
                if (visited.add(node)) {
                    final List<V> collection = new ObjectArrayList<>();
                    collection.add(node);
                    partitions.add(collection);
                    break;
                }
            }
        }
        int index = 0;
        while (visited.size() < graph.size()) {
            if (index == partitionCount) {
                index = 0;
            }
            final List<V> current = partitions.get(index);
            final V value = RandomUtil.getRandom(current, random);
            final Graph.Node<V, E> node = graph.get(value);
            final List<? extends Graph.Edge<V, E>> edges = node.getEdges();
            final Graph.Edge<V, E> edge = RandomUtil.getRandom(edges, random);
            if (edge != null) {
                final Graph.Node<V, E> other = edge.getOther(node);
                if (visited.add(other.getValue())) {
                    current.add(other.getValue());
                }
            }
            index++;
        }
        final List<IntList> adjacent = new ObjectArrayList<>(partitionCount);
        final List<Collection<Graph.Edge<V, E>>> outgoingEdges = new ObjectArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            adjacent.add(new IntArrayList());
            outgoingEdges.add(new ReferenceOpenHashSet<>());
        }
        for (int i = 0, partitionsSize = partitions.size(); i < partitionsSize; i++) {
            final Collection<V> partition = partitions.get(i);
            for (final V value : partition) {
                final Graph.Node<V, E> node = graph.get(value);
                for (final Graph.Edge<V, E> edge : node.getEdges()) {
                    final Graph.Node<V, E> other = edge.getOther(node);
                    if (!partition.contains(other.getValue())) {
                        outgoingEdges.get(i).add(edge);
                        for (int j = 0, size = partitions.size(); j < size; j++) {
                            final Collection<V> part = partitions.get(j);
                            if (part.contains(other.getValue())) {
                                adjacent.get(i).add(j);
                                break;
                            }
                        }
                    }
                }
            }
        }
        final List<Partition<V, E>> partitionList = new ObjectArrayList<>(partitionCount);
        for (int i = 0, partitionsSize = partitions.size(); i < partitionsSize; i++) {
            partitionList.add(new Partition<>(partitions.get(i), outgoingEdges.get(i)));
        }
        for (int i = 0, partitionListSize = partitionList.size(); i < partitionListSize; i++) {
            final Partition<V, E> vePartition = partitionList.get(i);
            final List<Partition<V, E>> adjacentList = new ObjectArrayList<>();
            for (final int integer : adjacent.get(i)) {
                adjacentList.add(partitionList.get(integer));
            }
            vePartition.setAdjacent(adjacentList);
        }
        final Map<Graph.Node<V, E>, Partition<V, E>> partitionMap = new Reference2ReferenceOpenHashMap<>();
        for (int i = 0, partitionsSize = partitions.size(); i < partitionsSize; i++) {
            final List<V> partition = partitions.get(i);
            for (final V v : partition) {
                partitionMap.put(graph.get(v), partitionList.get(i));
            }
        }
        return partitionMap;
    }


    public static class Partition<V, E> {
        private final Collection<V> vertices;
        private List<Partition<V, E>> adjacent;
        private final Collection<Graph.Edge<V, E>> outgoingEdges;

        public Partition(final Collection<V> vertices, final Collection<Graph.Edge<V, E>> outgoingEdges) {
            this.vertices = vertices;
            this.outgoingEdges = outgoingEdges;
        }

        private void setAdjacent(final List<Partition<V, E>> adjacent) {
            this.adjacent = adjacent;
        }

        public Collection<V> getVertices() {
            return vertices;
        }

        public List<Partition<V, E>> getAdjacent() {
            return adjacent;
        }

        public Collection<Graph.Edge<V, E>> getOutgoingEdges() {
            return outgoingEdges;
        }
    }

    private RandomPartition() {
    }
}
