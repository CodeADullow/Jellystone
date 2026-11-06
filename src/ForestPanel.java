import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class ForestPanel extends JPanel {

    static final Color EMPTY_COLOR = new Color(65, 50, 35);
    static final Color TREE_COLOR = new Color(45, 120, 55);
    static final Color FIRE_COLOR = new Color(210, 75, 25);
    static final Color LIGHTNING_COLOR = new Color(235, 210, 60);
    static final Color WIND_COLOR = new Color(0, 160, 255);

    private Forest forest;

    private Image treeImage;
    private Image fireImage;

    private Lightning lightning;
    private Image lightningImage;
    private WindVisualization windVisualization;

    private Cell lastLightningCell;
    private long lightningStartTime;

    public ForestPanel(Forest forest, Lightning lightning) {
        this.forest = forest;
        this.lightning = lightning;
        this.windVisualization = new WindVisualization(forest);

        System.out.println(new java.io.File("images/tree.png").exists());
        System.out.println(new java.io.File("images/fire.png").exists());
        System.out.println(new java.io.File("images/lightning.png").exists());

        treeImage = new ImageIcon("images/tree.png").getImage();
        fireImage = new ImageIcon("images/fire.png").getImage();
        lightningImage = new ImageIcon("images/lightning.png").getImage();

        addMovementKey("W", 0, -1);
        addMovementKey("A", -1, 0);
        addMovementKey("S", 0, 1);
        addMovementKey("D", 1, 0);
    }

    private void addMovementKey(
            String key,
            int changeX,
            int changeY) {

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(key),
                key
        );
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                forest.moveView(changeX, changeY);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Cell[][] grid = forest.getGrid();
        int rowCount = grid.length;
        int columnCount = grid[0].length;

        Cell currentLightningCell = null;

        if (lightning != null) {
            currentLightningCell = lightning.getLastStruckCell();

            if (currentLightningCell != lastLightningCell) {
                lastLightningCell = currentLightningCell;
                lightningStartTime = System.currentTimeMillis();
            }
        }

        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                Cell cell = grid[row][col];
                int x = col * getWidth() / columnCount;
                int nextX = (col + 1) * getWidth() / columnCount;
                int y = row * getHeight() / rowCount;
                int nextY = (row + 1) * getHeight() / rowCount;
                int cellWidth = nextX - x;
                int cellHeight = nextY - y;

                if (cellWidth <= 0 || cellHeight <= 0) {
                    continue;
                }

                boolean lightningActive = lightning != null
                        && cell == currentLightningCell
                        && System.currentTimeMillis() - lightningStartTime < 3000;

                if (lightningActive) {
                    g.setColor(LIGHTNING_COLOR);
                } else {
                    g.setColor(getCellColor(cell));
                }

                g.fillRect(x, y, cellWidth, cellHeight);

                if (lightningActive) {

                    drawCellImage(
                            g, lightningImage,
                            x, y, cellWidth, cellHeight
                    );
                }
                else {
                    if (!cell.hasTree()) {

                    }
                    else if (cell.getTree().isBurning()) {

                        drawCellImage(
                                g, fireImage,
                                x, y, cellWidth, cellHeight
                        );
                    }
                    else {

                        drawCellImage(
                                g, treeImage,
                                x, y, cellWidth, cellHeight
                        );
                    }
                }
            }
        }

        windVisualization.paintWindBorders(g, getWidth(), getHeight());
    }

    private Color getCellColor(Cell cell) {
        if (!cell.hasTree()) {
            return EMPTY_COLOR;
        }

        if (cell.getTree().isBurning()) {
            return FIRE_COLOR;
        }

        return TREE_COLOR;
    }

    private void drawCellImage(
            Graphics graphics,
            Image image,
            int x,
            int y,
            int width,
            int height) {

        int inset = Math.min(2, Math.min(width, height) / 4);

        graphics.drawImage(
                image,
                x + inset,
                y + inset,
                Math.max(1, width - inset * 2),
                Math.max(1, height - inset * 2),
                this
        );
    }
}
