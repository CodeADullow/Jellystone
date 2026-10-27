import java.util.Map;
import java.util.function.Predicate;

public class ForestStatisticsTest {

    public static void main(String[] args) {
        testCellCounts();
        testIterator();
        testTreePopulationByType();

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

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
