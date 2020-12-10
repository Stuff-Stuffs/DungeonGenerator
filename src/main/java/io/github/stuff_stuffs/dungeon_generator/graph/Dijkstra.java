package io.github.stuff_stuffs.dungeon_generator.graph;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.util.Set;
import java.util.function.ToIntFunction;

public class Dijkstra {
    public static <V, E> Reference2IntMap<V> findShortestPath(final Graph<V, E> graph, final V start, final ToIntFunction<E> costFunction) {
        final Set<V> visited = new ReferenceArraySet<>();
        final PriorityQueue<Node<V>> stack = new ObjectHeapPriorityQueue<>();
        final Reference2IntMap<V> map = new Reference2IntOpenHashMap<>();
        map.put(start, 0);
        visited.add(start);
        stack.enqueue(new Node<>(0, start));
        while (!stack.isEmpty()) {
            final Node<V> v = stack.dequeue();
            final V current = v.value;
            final Graph.Node<V, E> node = graph.get(current);
            for (final Graph.Edge<V, E> edge : node.getEdges()) {
                final V next = edge.getOther(node).getValue();
                if (visited.add(next)) {
                    final int nextCost = v.cost + costFunction.applyAsInt(edge.getValue());
                    map.put(next, nextCost);
                    visited.add(next);
                    stack.enqueue(new Node<>(nextCost, next));
                }
            }
        }
        return map;
    }

    private static class Node<V> implements Comparable<Node<V>> {
        public final int cost;
        public final V value;

        private Node(final int cost, final V node) {
            this.cost = cost;
            value = node;
        }

        @Override
        public int compareTo(final Node<V> o) {
            return Integer.compare(cost, o.cost);
        }
    }
}
