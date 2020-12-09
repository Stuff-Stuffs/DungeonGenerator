package io.github.stuff_stuffs.dungeon_generator.graph;

import io.github.stuff_stuffs.dungeon_generator.util.RandomUtil;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

public final class RandomWalker {
    public static <V, E> void walk(final Graph<V, E> graph, final V start, final BiConsumer<V, V> walker, final Random random) {
        final Set<V> visited = new ReferenceArraySet<>();
        final Stack<V> stack = new ObjectArrayList<>(graph.size());
        visited.add(start);
        stack.push(start);
        walker.accept(null, start);
        while (visited.size() < graph.size() && !stack.isEmpty()) {
            final V current = stack.top();
            final Set<V> neighbours = new ReferenceArraySet<>();
            final Graph.Node<V, E> node = graph.get(current);
            for (final Graph.Edge<V, E> edge : node.getEdges()) {
                if (!visited.contains(edge.getOther(node).getValue())) {
                    neighbours.add(edge.getOther(node).getValue());
                }
            }
            if (!neighbours.isEmpty()) {
                final V next = RandomUtil.getRandom(neighbours, random);
                walker.accept(current, next);
                visited.add(next);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
    }


    private RandomWalker() {
    }
}
