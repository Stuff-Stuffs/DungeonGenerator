package io.github.stuff_stuffs.dungeon_generator.util;

public class Vec2i {
    private final int x;
    private final int y;

    public Vec2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vec2i add(final Vec2i other) {
        return new Vec2i(x + other.x, y + other.y);
    }
}
