package io.github.stuff_stuffs.dungeon_generator.room;

import io.github.stuff_stuffs.dungeon_generator.util.Direction;

public class Connector {
    private final Direction direction;
    private int requirement = -1;

    public Connector(final Direction direction) {
        this.direction = direction;
    }

    public void setRequirement(final int requirement) {
        this.requirement = requirement;
    }

    public int getRequirement() {
        return requirement;
    }

    public Direction getDirection() {
        return direction;
    }
}
