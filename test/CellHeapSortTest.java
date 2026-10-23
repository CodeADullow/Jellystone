public class CellHeapSortTest {

    public static void main(String[] args) {
        testCellsAreSortedByYThenX();

        System.out.println("All CellHeapSort tests passed.");
    }

    private static void testCellsAreSortedByYThenX() {
        Cell[] cells = {
                new Cell(2, 1),
                new Cell(0, 0),
                new Cell(1, 0)
        };

        CellHeapSort.sort(cells);

        check(cells[0].getX() == 0 && cells[0].getY() == 0,
                "First cell should be (0, 0)");
        check(cells[1].getX() == 1 && cells[1].getY() == 0,
                "Second cell should be (1, 0)");
        check(cells[2].getX() == 2 && cells[2].getY() == 1,
                "Third cell should be (2, 1)");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
