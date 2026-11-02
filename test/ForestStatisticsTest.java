import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.function.Predicate;

public class ForestStatisticsTest {

    public static void main(String[] args) {
        testCellCounts();
        testIterator();
        testTreePopulationByType();
        testFireIntensityReduction();
        testTreeStatePartition();
        testParallelTreeHealthSummary();

        System.out.println("All Forest statistics tests passed.");
    }

    private static void testCellCounts() {
        Forest forest = new Forest(2, 2);

        for (Cell[] row : forest.getGrid()) {
            for (Cell cell : row) {
                cell.removeTree();
            }
        }

        forest.getGrid()[0][0].plantTree(new Tree(0.5));
        forest.getGrid()[1][1].plantTree(new Tree(0.5));
        forest.getGrid()[1][1].getTree().ignite();

        Predicate<Cell> burningCell =
                cell -> cell.hasTree() && cell.getTree().isBurning();

        check(
                forest.countCells(Cell::hasTree) == 2,
                "The stream should count cells containing trees"
        );
        check(
                forest.countCells(burningCell) == 1,
                "The supplied predicate should count burning cells"
        );
    }

    private static void testIterator() {
        Forest forest = new Forest(2, 3);
        int cellCount = 0;

        for (Cell cell : forest) {
            check(cell != null, "Iterator should return a cell");
            cellCount++;
        }

        check(cellCount == 6,
                "Iterator should visit every visible cell");
    }

    private static void testTreePopulationByType() {
        Forest forest = new Forest(1, 4);
        Cell[] cells = forest.getGrid()[0];

        for (Cell cell : cells) {
            cell.removeTree();
        }

        cells[0].plantTree(new Oak(0.5));
        cells[1].plantTree(new Oak(0.5));
        cells[2].plantTree(new Birch(0.5));
        cells[3].plantTree(new Eucalyptus(0.5));

        Map<String, Long> population =
                forest.getTreePopulationByType();

        check(population.get("OAK") == 2,
                "There should be two oak trees");
        check(population.get("BIRCH") == 1,
                "There should be one birch tree");
        check(population.get("EUCALYPTUS") == 1,
                "There should be one eucalyptus tree");
    }

    private static void testFireIntensityReduction() {
        Forest forest = new Forest(1, 3);
        Cell[] cells = forest.getGrid()[0];

        for (Cell cell : cells) {
            cell.removeTree();
        }

        Tree firstFire = new Oak(0.5);
        Tree secondFire = new Eucalyptus(0.5);
        firstFire.ignite();
        secondFire.ignite();
        cells[0].plantTree(firstFire);
        cells[1].plantTree(secondFire);
        cells[2].plantTree(new Birch(0.5));

        double expected = firstFire.getBurnIntensity()
                + secondFire.getBurnIntensity();

        check(
                Math.abs(forest.getTotalFireIntensity() - expected) < 0.0001,
                "Reduce should total the intensity of burning trees"
        );
    }

    private static void testTreeStatePartition() {
        Forest forest = new Forest(1, 3);
        Cell[] cells = forest.getGrid()[0];

        for (Cell cell : cells) {
            cell.removeTree();
        }

        Tree burningTree = new Oak(0.5);
        burningTree.ignite();
        cells[0].plantTree(burningTree);
        cells[1].plantTree(new Birch(0.5));
        cells[2].plantTree(new Eucalyptus(0.5));

        Map<Boolean, Long> states = forest.getTreeStateCounts();

        check(states.get(true) == 1,
                "One tree should be in the burning partition");
        check(states.get(false) == 2,
                "Two trees should be in the non-burning partition");
    }

    private static void testParallelTreeHealthSummary() {
        Forest forest = new Forest(1, 3);
        Cell[] cells = forest.getGrid()[0];

        for (Cell cell : cells) {
            cell.removeTree();
        }

        Oak damagedOak = new Oak(0.5);
        damagedOak.damage(50.0);
        cells[0].plantTree(damagedOak);
        cells[1].plantTree(new Oak(0.5));

        Eucalyptus eucalyptus = new Eucalyptus(0.5);
        eucalyptus.damage(120.0);
        cells[2].plantTree(eucalyptus);

        Map<String, DoubleSummaryStatistics> health =
                forest.getTreeHealthByType();

        check(health.get("OAK").getCount() == 2,
                "The parallel summary should include both oak trees");
        check(health.get("OAK").getAverage() == 75.0,
                "Average oak health should be 75 percent");
        check(health.get("EUCALYPTUS").getAverage() == 50.0,
                "Average eucalyptus health should be 50 percent");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
