package io.github.stuff_stuffs.dungeon_generator.util;

import io.github.stuff_stuffs.dungeon_generator.room.Room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoomArray {
    private final int radius;
    private final Room[] rooms;

    public RoomArray(final int radius) {
        this.radius = radius;
        rooms = new Room[this.radius * this.radius];
    }

    public Room get(final Vec2i vec2i) {
        return get(vec2i.getX(), vec2i.getY());
    }

    public Room get(final int x, final int y) {
        return rooms[x * radius + y];
    }

    public void set(final Vec2i vec2i, final Room room) {
        set(vec2i.getX(), vec2i.getY(), room);
    }

    public void set(final int x, final int y, final Room room) {
        rooms[x * radius + y] = room;
    }

    public Vec2i getPos(final Room room) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                if (rooms[i * radius + j] == room) {
                    return new Vec2i(i, j);
                }
            }
        }
        return null;
    }

    public Collection<Direction> getEmptyDirections(final Vec2i vec2i) {
        return getEmptyDirections(vec2i.getX(), vec2i.getY());
    }

    public Collection<Direction> getEmptyDirections(final int x, final int y) {
        final List<Direction> directions = new ArrayList<>(4);
        if (isValid(x, y - 1)) {
            directions.add(Direction.NORTH);
        }
        if (isValid(x, y + 1)) {
            directions.add(Direction.SOUTH);
        }
        if (isValid(x + 1, y)) {
            directions.add(Direction.EAST);
        }
        if (isValid(x - 1, y)) {
            directions.add(Direction.WEST);
        }
        return directions;
    }

    public boolean isFilled(final int x, final int y) {
        if (!(x >= 0 && x < radius && y >= 0 && y < radius)) {
            return false;
        } else {
            return rooms[x * radius + y] != null;
        }
    }

    private boolean isValid(final int x, final int y) {
        if (!(x >= 0 && x < radius && y >= 0 && y < radius)) {
            return false;
        } else {
            return rooms[x * radius + y] == null;
        }
    }
}
