import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class ForestPanel extends JPanel {

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

        int cellWidth = getWidth() / grid[0].length;
        int cellHeight = getHeight() / grid.length;

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

                g.setColor(Color.BLACK);

                g.fillRect(
                    col * cellWidth,
                    row * cellHeight,
                    cellWidth,
                    cellHeight
                );

                if (lightning != null &&
                    cell == currentLightningCell &&
                    System.currentTimeMillis() - lightningStartTime < 3000) {

                    g.drawImage(
                        lightningImage,
                        col * cellWidth + 2,
                        row * cellHeight + 2,
                        cellWidth - 4,
                        cellHeight - 4,
                        this
                    );
                }
                else {
                    if (!cell.hasTree()) {

                        // Empty cell stays black

                    }
                    else if (cell.getTree().isBurning()) {

                        g.drawImage(
                            fireImage,
                            col * cellWidth + 2,
                            row * cellHeight + 2,
                            cellWidth - 4,
                            cellHeight - 4,
                            this
                        );
                    }
                    else {

                        g.drawImage(
                            treeImage,
                            col * cellWidth + 2,
                            row * cellHeight + 2,
                            cellWidth - 4,
                            cellHeight - 4,
                            this
                        );
                    }
                }
            }
        }

        windVisualization.paintWindBorders(
                g,
                getWidth(),
                getHeight()
        );
    }
}
