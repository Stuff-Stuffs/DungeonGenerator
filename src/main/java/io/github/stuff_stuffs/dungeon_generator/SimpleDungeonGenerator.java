package io.github.stuff_stuffs.dungeon_generator;

import io.github.stuff_stuffs.dungeon_generator.generator.GraphGenerator;
import io.github.stuff_stuffs.dungeon_generator.generator.SimpleGraphGenerator;
import io.github.stuff_stuffs.dungeon_generator.graph.*;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;
import io.github.stuff_stuffs.dungeon_generator.util.RandomUtil;
import io.github.stuff_stuffs.dungeon_generator.util.Xoroshiro256;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SimpleDungeonGenerator implements DungeonGenerator {
    private final long seed;
    private Dungeon dungeon;

    public SimpleDungeonGenerator(final long seed) {
        this.seed = seed;
    }

    @Override
    public Dungeon generate(final int size) {
        final long time = System.currentTimeMillis();
        final GraphGenerator graphGenerator = new SimpleGraphGenerator();
        final MutableGraph<Room, Connector> graph = graphGenerator.generateGraph(size - 1, seed);
        final Map<Graph.Node<Room, Connector>, RandomPartition.Partition<Room, Connector>> partitionMap = RandomPartition.partition(graph, (int) Math.ceil(Math.sqrt(size)), new Xoroshiro256(seed + 1));
        final Collection<RandomPartition.Partition<Room, Connector>> partitions = partitionMap.values();
        final Xoroshiro256 random = new Xoroshiro256(seed + 2);
        final MutableGraph<RandomPartition.Partition<Room, Connector>, Integer> superGraph = new ReferenceMutableMapGraph<>();
        for (final RandomPartition.Partition<Room, Connector> partition : partitions) {
            superGraph.insert(partition);
        }
        int nextKey = 1;
        for (final RandomPartition.Partition<Room, Connector> partition : partitions) {
            for (final RandomPartition.Partition<Room, Connector> adjacent : partition.getAdjacent()) {
                superGraph.addEdge(partition, adjacent, nextKey++, false);
            }
        }
        for (final Graph.Edge<Room, Connector> edge : graph.getEdges()) {
            final RandomPartition.Partition<Room, Connector> firstPartition = partitionMap.get(edge.getFirst());
            final RandomPartition.Partition<Room, Connector> secondPartition = partitionMap.get(edge.getSecond());
            if (firstPartition != secondPartition) {
                edge.getValue().setRequirement(superGraph.getEdge(firstPartition, secondPartition).getValue());
            }
        }
        for (final RandomPartition.Partition<Room, Connector> partition : partitionMap.values()) {
            final Color color = new Color(random.nextInt());
            for (final Room vertex : partition.getVertices()) {
                vertex.setColour(color.getRGB());
            }
        }
        Room startRoom = null;
        for (final Room room : graph) {
            if (room.getName().equals("start")) {
                startRoom = room;
            }
        }
        if (startRoom == null) {
            throw new RuntimeException();
        }
        setupRequirements(partitionMap, graph, startRoom, random);
        System.out.println((System.currentTimeMillis() - time) / 1000d);
        return dungeon = new Dungeon() {
            @Override
            public Graph<Room, Connector> getGraph() {
                return graph;
            }

            @Override
            public int getSize() {
                return 255;
            }
        };
    }

    private void setupRequirements(final Map<Graph.Node<Room, Connector>, RandomPartition.Partition<Room, Connector>> partitionMap, final MutableGraph<Room, Connector> graph, final Room start, final Random random) {
        final MutableGraph<RandomPartition.Partition<Room, Connector>, Object> superGraph = new ReferenceMutableMapGraph<>();
        for (final RandomPartition.Partition<Room, Connector> partition : partitionMap.values()) {
            superGraph.insert(partition);
        }
        for (final RandomPartition.Partition<Room, Connector> partition : partitionMap.values()) {
            for (final RandomPartition.Partition<Room, Connector> neighbour : partition.getAdjacent()) {
                superGraph.addEdge(partition, neighbour, null, false);
            }
        }
        final ReferenceArrayList<RandomPartition.Partition<Room, Connector>> visited = new ReferenceArrayList<>();
        final Int2ObjectMap<List<RandomPartition.Partition<Room, Connector>>> keyMap = new Int2ObjectArrayMap<>();
        RandomWalker.walk(superGraph, partitionMap.get(graph.get(start)), (prev, current) -> {
            if (prev != null) {
                final List<Graph.Edge<Room, Connector>> edges = getEdges(partitionMap, prev, current);
                for (final Graph.Edge<Room, Connector> edge : edges) {
                    final Connector connector = edge.getValue();
                    if (!keyMap.containsKey(connector.getRequirement())) {
                        keyMap.put(connector.getRequirement(), new ReferenceArrayList<>(visited));
                    }
                }
            }
            visited.add(current);
        }, random);
        for (final Int2ObjectMap.Entry<List<RandomPartition.Partition<Room, Connector>>> entry : keyMap.int2ObjectEntrySet()) {
            setupKeys(entry.getIntKey(), entry.getValue(), random);
        }
        for (final RandomPartition.Partition<Room, Connector> partition : partitionMap.values()) {
            final Collection<Graph.Edge<Room, Connector>> edgesToDelete = new ReferenceArraySet<>();
            for (final Graph.Edge<Room, Connector> outgoingEdge : partition.getOutgoingEdges()) {
                if (outgoingEdge.getValue().getRequirement() != 0 && !keyMap.containsKey(outgoingEdge.getValue().getRequirement())) {
                    edgesToDelete.add(outgoingEdge);
                }
            }
            for (final Graph.Edge<Room, Connector> edge : edgesToDelete) {
                graph.removeEdge(edge.getFirst().getValue(), edge.getSecond().getValue());
            }
        }
    }

    private void setupKeys(final int key, final List<RandomPartition.Partition<Room, Connector>> partitions, final Random random) {
        while (true) {
            final RandomPartition.Partition<Room, Connector> partition = RandomUtil.getRandom(partitions, random);
            final Room room = RandomUtil.getRandom(partition.getVertices(), random);
            if (room.getProvidedRequirement() == 0) {
                room.setProvidedRequirement(key);
                break;
            }
        }
    }

    private List<Graph.Edge<Room, Connector>> getEdges(final Map<Graph.Node<Room, Connector>, RandomPartition.Partition<Room, Connector>> partitionMap, final RandomPartition.Partition<Room, Connector> first, final RandomPartition.Partition<Room, Connector> second) {
        final ObjectArrayList<Graph.Edge<Room, Connector>> edges = new ObjectArrayList<>(3);
        for (final Graph.Edge<Room, Connector> edge : first.getOutgoingEdges()) {
            final Graph.Node<Room, Connector> firstNode = edge.getFirst();
            final Graph.Node<Room, Connector> secondNode = edge.getSecond();
            final RandomPartition.Partition<Room, Connector> firstPartition = partitionMap.get(firstNode);
            final RandomPartition.Partition<Room, Connector> secondPartition = partitionMap.get(secondNode);
            if ((firstPartition == first & secondPartition == second) | (firstPartition == second && secondPartition == first)) {
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Dungeon getDungeon() {
        return dungeon;
    }
}
