package io.github.stuff_stuffs.dungeon_generator.room;

import io.github.stuff_stuffs.dungeon_generator.util.Vec2i;

import java.awt.*;

public class Room {
    private final String name;
    private final Vec2i pos;
    private int colour = Color.GREEN.getRGB();
    private int providedRequirement = 0;

    public Room(final String name, Vec2i pos) {
        this.name = name;
        this.pos = pos;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public int getColour() {
        return colour;
    }

    public int getProvidedRequirement() {
        return providedRequirement;
    }

    public void setProvidedRequirement(int providedRequirement) {
        this.providedRequirement = providedRequirement;
    }

    public String getName() {
        return name;
    }

    public Vec2i getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return name;
    }
}
