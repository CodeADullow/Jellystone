public class ForestNavigationTest {

    public static void main(String[] args) {
        testMovementAndStoredCells();

        System.out.println("All Forest navigation tests passed.");
    }

    private static void testMovementAndStoredCells() {
        Forest forest = new Forest(2, 3);
        Cell originalTopLeft = forest.getGrid()[0][0];
        Cell originalTopMiddle = forest.getGrid()[0][1];

        forest.moveView(1, 0);

        check(forest.getOriginX() == 1, "View should move right");
        check(
                forest.getGrid()[0][0] == originalTopMiddle,
                "Overlapping cells should be reused"
        );

        forest.moveView(-1, 0);

        check(
                forest.getGrid()[0][0] == originalTopLeft,
                "Returning should restore the same cell object"
        );

        forest.moveView(-1, -1);
        check(
                forest.getGrid()[0][0].getX() == -1
                        && forest.getGrid()[0][0].getY() == -1,
                "The world should support negative coordinates"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
