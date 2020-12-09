package io.github.stuff_stuffs.dungeon_generator.util;

public enum Direction {
    NORTH(new Vec2i(1, 0)),
    EAST(new Vec2i(0, 1)),
    SOUTH(new Vec2i(-1, 0)),
    WEST(new Vec2i(0, -1));

    private final Vec2i offset;

    Direction(final Vec2i offset) {
        this.offset = offset;
    }

    public Vec2i getOffset() {
        return offset;
    }

    public static Direction getOpposite(Direction direction) {
        switch (direction) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            default:
                throw new NullPointerException();
        }
    }
}
