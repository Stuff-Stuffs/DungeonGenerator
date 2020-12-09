package io.github.stuff_stuffs.dungeon_generator.generator;

import io.github.stuff_stuffs.dungeon_generator.graph.Graph;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;

public interface GraphGenerator {
    Graph<Room, Connector> generateGraph(int size, long seed);
}
