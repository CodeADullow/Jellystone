import javax.swing.JPanel;
import java.awt.*;

public class WindVisualization extends JPanel {

    private Forest forest;

    public WindVisualization(Forest forest) {
        if (forest == null) {
            throw new WindArgumentException("Forest must not be null");
        }

        this.forest = forest;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        paintWindBorders(graphics, getWidth(), getHeight());
    }

    public void paintWindBorders(
            Graphics graphics,
            int width,
            int height) {

        Cell[][] grid = forest.getGrid();

        if (grid.length == 0 || grid[0].length == 0) {
            return;
        }

        Graphics2D borderGraphics = (Graphics2D) graphics.create();
        borderGraphics.setColor(ForestPanel.WIND_COLOR);
        borderGraphics.setStroke(new BasicStroke(2.0f));

        int rowCount = grid.length;
        int columnCount = grid[0].length;
        Rectangle gridBounds = ForestPanel.calculateGridBounds(
                width,
                height,
                rowCount,
                columnCount
        );

        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                if (grid[row][col].getWindExposure() <= 0.0) {
                    continue;
                }

                int x = gridBounds.x
                        + col * gridBounds.width / columnCount;
                int nextX = gridBounds.x
                        + (col + 1) * gridBounds.width / columnCount;
                int y = gridBounds.y
                        + row * gridBounds.height / rowCount;
                int nextY = gridBounds.y
                        + (row + 1) * gridBounds.height / rowCount;
                int cellWidth = nextX - x;
                int cellHeight = nextY - y;

                if (cellWidth > 0 && cellHeight > 0) {
                    borderGraphics.drawRect(
                            x,
                            y,
                            cellWidth - 1,
                            cellHeight - 1
                    );
                }
            }
        }

        borderGraphics.dispose();
    }
}
