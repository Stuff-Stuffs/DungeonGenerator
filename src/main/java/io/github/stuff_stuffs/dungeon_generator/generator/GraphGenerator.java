package io.github.stuff_stuffs.dungeon_generator.generator;

import io.github.stuff_stuffs.dungeon_generator.graph.MutableGraph;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;

public interface GraphGenerator {
    MutableGraph<Room, Connector> generateGraph(int size, long seed);
}
