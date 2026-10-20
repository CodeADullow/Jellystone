import java.util.function.Predicate;

public class ForestStatisticsTest {

    public static void main(String[] args) {
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

        System.out.println("All Forest statistics tests passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
