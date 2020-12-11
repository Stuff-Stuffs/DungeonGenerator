package io.github.stuff_stuffs.dungeon_generator;

public interface DungeonGenerator {
    DungeonData generate(int size);
    DungeonData getDungeon();
}
