public class ForestNavigationTest {

    public static void main(String[] args) {
        testMovementAndStoredCells();
        testReset();

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

    private static void testReset() {
        Forest forest = new Forest(2, 3);
        Cell originalTopLeft = forest.getGrid()[0][0];

        forest.moveView(4, -2);
        forest.reset();

        check(forest.getOriginX() == 0 && forest.getOriginY() == 0,
                "Reset should return the viewport to the origin");
        check(forest.getGrid()[0][0].getX() == 0
                        && forest.getGrid()[0][0].getY() == 0,
                "Reset should rebuild the original visible coordinates");
        check(forest.getGrid()[0][0] != originalTopLeft,
                "Reset should create a new forest rather than reuse old cells");
        check(forest.getSimulationStateName().equals("RESET"),
                "Reset should stop the simulation in the reset state");

        forest.toggleSimulationState();
        check(forest.getSimulationStateName().equals("RUNNING"),
                "Pause / Resume should start a reset simulation");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
