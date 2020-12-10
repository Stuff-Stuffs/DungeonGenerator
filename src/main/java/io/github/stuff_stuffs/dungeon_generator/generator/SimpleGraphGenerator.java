package io.github.stuff_stuffs.dungeon_generator.generator;

import io.github.stuff_stuffs.dungeon_generator.graph.Graph;
import io.github.stuff_stuffs.dungeon_generator.graph.MutableGraph;
import io.github.stuff_stuffs.dungeon_generator.graph.ReferenceMutableMapGraph;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;
import io.github.stuff_stuffs.dungeon_generator.tree.MutableTree;
import io.github.stuff_stuffs.dungeon_generator.tree.ReferenceMutableTree;
import io.github.stuff_stuffs.dungeon_generator.util.*;

import java.util.*;

public class SimpleGraphGenerator implements GraphGenerator {
    @Override
    public MutableGraph<Room, Connector> generateGraph(final int size, final long seed) {
        final Room start = new Room("start", new Vec2i(0, 0));
        final List<Room> rooms = new ArrayList<>();
        int s = (int) Math.ceil(Math.sqrt(size + 1));
        final RoomArray roomArray = new RoomArray(s);
        final MutableTree<Room, Connector> tree = new ReferenceMutableTree<>(start);
        rooms.add(start);
        roomArray.set(0, 0, start);
        final Random random = new Xoroshiro256(seed);
        for (int i = 0; i < size; i++) {
            while (true) {
                final Room room = RandomUtil.getRandom(rooms, random);
                final Vec2i pos = roomArray.getPos(room);
                final Collection<Direction> directions = roomArray.getEmptyDirections(pos);
                if (directions.size() > 0) {
                    final Direction direction = RandomUtil.getRandom(directions, random);
                    final Vec2i nextPos = pos.add(direction.getOffset());
                    final Room next = new Room("" + i, nextPos);
                    roomArray.set(nextPos, next);
                    tree.insert(room, next, new Connector(direction));
                    rooms.add(next);
                    break;
                }
            }
        }
        MutableGraph<Room, Connector> graph = MutableGraph.fromTree(tree, ReferenceMutableMapGraph::new);
        List<Direction> directionList = Arrays.asList(Direction.values());
        for (int i = 0; i < size/2; i++) {
            int x = random.nextInt(s);
            int y = random.nextInt(s);
            Direction direction = RandomUtil.getRandom(directionList, random);
            if(roomArray.isFilled(x,y) && roomArray.isFilled(x + direction.getOffset().getX(), y + direction.getOffset().getY())) {
                Room first = roomArray.get(x,y);
                Room second = roomArray.get(x + direction.getOffset().getX(), y + direction.getOffset().getY());
                graph.addEdge(first, second, new Connector(direction), false);
            }
        }
        return graph;
    }
}
