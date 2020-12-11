package io.github.stuff_stuffs.dungeon_generator;

import io.github.stuff_stuffs.dungeon_generator.graph.Graph;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;

public interface DungeonData {
    Graph<Room, Connector> getGraph();

    int getSize();

    int getRequirements();

    int getLength();
}
