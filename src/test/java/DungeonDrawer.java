import io.github.stuff_stuffs.dungeon_generator.Dungeon;
import io.github.stuff_stuffs.dungeon_generator.graph.Graph;
import io.github.stuff_stuffs.dungeon_generator.room.Connector;
import io.github.stuff_stuffs.dungeon_generator.room.Room;
import io.github.stuff_stuffs.dungeon_generator.util.Vec2i;
import it.unimi.dsi.fastutil.HashCommon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class DungeonDrawer {
    private final double scale = 10;
    private final double roomSize = 3;
    private Vec2i pos = new Vec2i(0, 0);
    private Mode mode = Mode.KEY;

    public Mode getMode() {
        return mode;
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public void setPos(final Vec2i pos) {
        this.pos = pos;
    }

    public Vec2i getPos() {
        return pos;
    }

    public void draw(final Graphics2D graphics, final Dungeon dungeon) {
        graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        for (final Graph.Edge<Room, Connector> edge : dungeon.getGraph().getEdges()) {
            drawEdge(edge, graphics);
        }
        for (final Room room : dungeon.getGraph()) {
            drawRoom(room, graphics);
        }
    }

    private void drawEdge(final Graph.Edge<Room, Connector> edge, final Graphics2D graphics) {
        final Vec2i firstPos = edge.getFirst().getValue().getPos();
        final AffineTransform affineTransform = AffineTransform.getScaleInstance(scale, scale);
        affineTransform.translate((firstPos.getX() + pos.getX()) * scale, (firstPos.getY() + pos.getY()) * scale);
        final AffineTransform pre = graphics.getTransform();
        graphics.setTransform(affineTransform);
        final Shape shape = new Line2D.Double(0, 0, edge.getValue().getDirection().getOffset().getX() * scale, edge.getValue().getDirection().getOffset().getY() * scale);
        graphics.setColor(new Color(edge.getValue().getRequirement() == -1 ? Color.BLACK.getRGB() : HashCommon.mix(edge.getValue().getRequirement())));
        graphics.draw(shape);
        graphics.setTransform(pre);
    }

    private void drawRoom(final Room room, final Graphics2D graphics) {
        final Vec2i vec2i = room.getPos();
        final AffineTransform affineTransform = AffineTransform.getScaleInstance(scale, scale);
        affineTransform.translate((vec2i.getX() + pos.getX()) * scale, (vec2i.getY() + pos.getY()) * scale);
        final AffineTransform pre = graphics.getTransform();
        graphics.setTransform(affineTransform);
        final Shape shape = new RoundRectangle2D.Double(-roomSize / 2, -roomSize / 2, roomSize, roomSize, roomSize / 8, roomSize / 8);
        if (mode == Mode.KEY) {
            graphics.setColor(new Color(room.getColour()));
            graphics.draw(shape);
            graphics.fill(shape);
            if (room.getProvidedRequirement() != 0) {
                graphics.setColor(new Color(HashCommon.mix(room.getProvidedRequirement())));
                graphics.fill(shape);
            }
        } else if (mode == Mode.DIFFICULTY) {
            graphics.setColor(getDifficultyColor(room.getDifficulty()));
            graphics.draw(shape);
            graphics.fill(shape);
        }
        graphics.setColor(Color.BLACK);
        graphics.scale(1 / 10d, 1 / 10d);
        graphics.drawString(room.getName(), -8, 6);
        graphics.setTransform(pre);
    }

    public Color getDifficultyColor(final double val) {
        final float normalized = (float)((1-Math.min(Math.max(val, 0), 1)) * (2/3d));
        return Color.getHSBColor(normalized, 1, 1);
    }

    public enum Mode {
        KEY,
        DIFFICULTY;
        private static final Mode[] MODES = Mode.values();

        public static Mode cycle(final Mode mode) {
            return MODES[(mode.ordinal() + 1) % MODES.length];
        }
    }
}
