import io.github.stuff_stuffs.dungeon_generator.DungeonGenerator;
import io.github.stuff_stuffs.dungeon_generator.SimpleDungeonGenerator;
import io.github.stuff_stuffs.dungeon_generator.util.Vec2i;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Main extends JPanel {
    private BufferedImage buffer;
    private Graphics2D graphics;
    private Dimension dimensions;

    private DungeonGenerator dungeonGen;
    private final DungeonDrawer dungeonDrawer = new DungeonDrawer();

    private Thread generatorThread;

    public static void main(final String[] args) {
        final JFrame frame = new JFrame("Dungeon generator");
        final Main panel = new Main();
        panel.setPreferredSize(new Dimension(1024, 768));
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    panel.regenerate(System.nanoTime());
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    final DungeonDrawer dungeonView = panel.dungeonDrawer;
                    final Vec2i pos = dungeonView.getPos();
                    dungeonView.setPos(pos.add(new Vec2i(1, 0)));
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    final DungeonDrawer dungeonView = panel.dungeonDrawer;
                    final Vec2i pos = dungeonView.getPos();
                    dungeonView.setPos(pos.add(new Vec2i(-1, 0)));
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    final DungeonDrawer dungeonView = panel.dungeonDrawer;
                    final Vec2i pos = dungeonView.getPos();
                    dungeonView.setPos(pos.add(new Vec2i(0, 1)));
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    final DungeonDrawer dungeonView = panel.dungeonDrawer;
                    final Vec2i pos = dungeonView.getPos();
                    dungeonView.setPos(pos.add(new Vec2i(0, -1)));
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    final DungeonDrawer dungeonDrawer = panel.dungeonDrawer;
                    dungeonDrawer.setMode(DungeonDrawer.Mode.KEY);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    final DungeonDrawer dungeonDrawer = panel.dungeonDrawer;
                    dungeonDrawer.setMode(DungeonDrawer.Mode.DIFFICULTY);
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    final DungeonDrawer dungeonDrawer = panel.dungeonDrawer;
                    dungeonDrawer.setMode(DungeonDrawer.Mode.cycle(dungeonDrawer.getMode()));
                }
                panel.paint(frame.getGraphics());
            }
        });

        frame.setVisible(true);
    }

    private void regenerate(final long seed) {
        if (generatorThread == null) {
            dungeonGen = new SimpleDungeonGenerator(seed);
            dungeonGen.generate(512);
            generatorThread = null;
        }
    }

    @Override
    public void paint(final Graphics graphics) {
        fixBuffer();

        this.graphics.setColor(Color.WHITE);
        this.graphics.fillRect(0, 0, dimensions.width, dimensions.height);
        if (dungeonDrawer != null && dungeonGen != null && dungeonGen.getDungeon() != null) {
            dungeonDrawer.draw(this.graphics, dungeonGen.getDungeon());
        }
        graphics.drawImage(buffer, 0, 0, this);
    }

    private void fixBuffer() {
        if (!getSize().equals(dimensions)) {
            dimensions = new Dimension(getSize());
            buffer = new BufferedImage(dimensions.width, dimensions.height, BufferedImage.TYPE_INT_ARGB);
            graphics = buffer.createGraphics();
        }
    }
}
