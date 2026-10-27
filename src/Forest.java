import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Forest implements Iterable<Cell> {

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
    private SimulationState simulationState = new RunningState();

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

    @Override
    public Iterator<Cell> iterator() {
        return new ForestIterator();
    }

    private class ForestIterator implements Iterator<Cell> {

        private int row;
        private int column;

        @Override
        public boolean hasNext() {
            return row < grid.length;
        }

        @Override
        public Cell next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more forest cells");
            }

            Cell cell = grid[row][column];
            column++;

            if (column == grid[row].length) {
                column = 0;
                row++;
            }

            return cell;
        }
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

    public void toggleSimulationState() {
        if (simulationState instanceof RunningState) {
            simulationState = new PausedState();
        } else {
            simulationState = new RunningState();
        }
    }

    public String getSimulationStateName() {
        return simulationState.getName();
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

    // MEMORY RED FLAG
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
            cell.plantTree(createRandomTree(spreadability));
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

    private Tree createRandomTree(double spreadability) {
        if (random.nextBoolean()) {
            return new Oak(spreadability);
        }

        return new Eucalyptus(spreadability);
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

    // Groups visible trees by species and counts each group.
    public Map<String, Long> getTreePopulationByType() {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Cell::hasTree)
                .map(Cell::getTree)
                .collect(Collectors.groupingBy(
                        Tree::getTreeType,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public void update() {
        simulationState.update();
    }

    private void performUpdate() {

        spreadFire();
        advanceBurningTrees();
    }

    private interface SimulationState {

        void update();

        String getName();
    }

    private class RunningState implements SimulationState {
        @Override
        public void update() {
            performUpdate();
        }

        @Override
        public String getName() {
            return "RUNNING";
        }
    }

    private class PausedState implements SimulationState {
        @Override
        public void update() {
        }

        @Override
        public String getName() {
            return "PAUSED";
        }
    }


    private void advanceBurningTrees() {

        for (Cell cell : this) {
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
