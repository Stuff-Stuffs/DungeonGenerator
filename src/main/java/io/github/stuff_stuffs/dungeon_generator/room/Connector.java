package io.github.stuff_stuffs.dungeon_generator.room;

import io.github.stuff_stuffs.dungeon_generator.util.Direction;

public class Connector {
    private final Direction direction;
    private final Room from;
    private int requirement = -1;

    public Connector(final Direction direction, final Room from) {
        this.direction = direction;
        this.from = from;
    }

    public void setRequirement(final int requirement) {
        this.requirement = requirement;
    }

    public int getRequirement() {
        return requirement;
    }

    public Direction getDirection(final Room perspective) {
        if (from == perspective) {
            return direction;
        } else {
            return Direction.getOpposite(direction);
        }
    }
}
