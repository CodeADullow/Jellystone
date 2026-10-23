import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

public class Forest {

    private Cell[][] grid;

    private double temperature;
    private double rainfall;
    private double aridity;

    private Wind wind;

    private Random random = new Random();
    private Cell[] worldCells = new Cell[100];
    private int worldCellCount;
    private int originX;
    private int originY;

    public Forest(int rows, int columns) {

        grid = new Cell[rows][columns];

        temperature = 25.0;
        rainfall = 0.2;
        aridity = 0.5;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                grid[row][col] = getOrCreateCell(col, row);
            }
        }
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
        wind.applyWind(this);
    }

    public Wind getWind() {
        return wind;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public void moveView(int changeX, int changeY) {
        originX += changeX;
        originY += changeY;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int worldX = originX + col;
                int worldY = originY + row;
                grid[row][col] = getOrCreateCell(worldX, worldY);
            }
        }

        if (wind != null) {
            wind.applyWind(this);
        }
    }

    public Cell[] getVisibleCellsSorted() {
        Cell[] cells = Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .toArray(Cell[]::new);

        CellHeapSort.sort(cells);
        return cells;
    }

    private Cell getOrCreateCell(int worldX, int worldY) {
        for (int index = 0; index < worldCellCount; index++) {
            Cell storedCell = worldCells[index];

            if (storedCell.getX() == worldX
                    && storedCell.getY() == worldY) {
                return storedCell;
            }
        }

        Cell cell = new Cell(worldX, worldY);

        if (random.nextDouble() < 0.8) {
            double spreadability =
                    0.3 + random.nextDouble() * 0.7;
            cell.plantTree(new Tree(spreadability));
        }

        if (worldCellCount == worldCells.length) {
            worldCells = Arrays.copyOf(
                    worldCells,
                    worldCells.length * 2
            );
        }

        worldCells[worldCellCount] = cell;
        worldCellCount++;
        return cell;
    }


    // Grid is simplified into a 2D array of cells, stream used to compile list of cells that match predicate, return count.
    public long countCells(Predicate<Cell> condition) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Cell condition must not be null"
            );
        }

        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(condition)
                .count();
    }

    public void update() {

        spreadFire();
        advanceBurningTrees();
    }


    private void advanceBurningTrees() {

        for (Cell[] row : grid) {

            for (Cell cell : row) {

                if (!cell.hasTree()) {
                    continue;
                }

                Tree tree = cell.getTree();

                if (tree.isBurning()) {
                    double windExposure = Math.max(
                            0.0,
                            Math.min(1.0, cell.getWindExposure())
                    );

                    tree.advanceBurning(1.0 + windExposure);
                }

                if (!tree.isAlive()) {
                    cell.removeTree();
                }
            }
        }
    }

    
    private void spreadFire() {

        boolean[][] igniteNext =
                new boolean[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                Cell cell = grid[row][col];

                if (cell.hasTree() &&
                    cell.getTree().isBurning()) {

                    trySpread(row, col, row - 1, col, igniteNext);
                    trySpread(row, col, row + 1, col, igniteNext);
                    trySpread(row, col, row, col - 1, igniteNext);
                    trySpread(row, col, row, col + 1, igniteNext);
                    trySpread(row, col, row - 1, col - 1, igniteNext);
                    trySpread(row, col, row - 1, col + 1, igniteNext);
                    trySpread(row, col, row + 1, col - 1, igniteNext);
                    trySpread(row, col, row + 1, col + 1, igniteNext);
                }
            }
        }

        // Ignite AFTER checking the entire grid
        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                if (igniteNext[row][col]) {
                    grid[row][col].getTree().ignite();
                }
            }
        }
    }

    private void trySpread(
        int sourceRow,
        int sourceCol,
        int row,
        int col,
        boolean[][] igniteNext) {

        if (row < 0 ||
            row >= grid.length ||
            col < 0 ||
            col >= grid[0].length) {

            return;
        }

        Cell target = grid[row][col];

        if (!target.hasTree()) {
            return;
        }

        if (target.getTree().isBurning()) {
            return;
        }

        double chance =
                target.getTree().getSpreadability();

        if (wind != null) {

            chance += wind.getSpreadModifier(
                    sourceCol,
                    sourceRow,
                    col,
                    row
            );
}

        chance += aridity * 0.2;

        chance += (temperature / 100.0) * 0.2;

        chance -= rainfall * 0.2;

        chance = Math.max(0.0, Math.min(1.0, chance));

        if (random.nextDouble() < chance) {
            igniteNext[row][col] = true;
        }
    }

    public void display() {

        for (Cell[] row : grid) {

            for (Cell cell : row) {

                if (!cell.hasTree()) {
                    System.out.print(". ");

                } else if (cell.getTree().isBurning()) {
                    System.out.print("X ");

                } else {
                    System.out.print("T ");
                }
            }

            System.out.println();
        }
    }
}
