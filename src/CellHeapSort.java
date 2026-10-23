public class CellHeapSort {

    //https://www.tutorialspoint.com/data_structures_algorithms/heap_sort_algorithm.htm
    // https://www.geeksforgeeks.org/dsa/heap-sort/
    //
    private CellHeapSort() {
        // no object
    }

    public static void sort(Cell[] cells) {
        int size = cells.length;

        for (int index = size / 2 - 1; index >= 0; index--) {
            heapify(cells, size, index);
        }

        for (int end = size - 1; end > 0; end--) {
            swap(cells, 0, end);
            heapify(cells, end, 0);
        }
    }

    private static void heapify(Cell[] cells, int size, int root) {
        int large = root;
        int l = root * 2 + 1;
        int r = root * 2 + 2;

        if (l < size && compare(cells[l], cells[large]) > 0) {
            large = l;
        }
        if (r < size && compare(cells[r], cells[large]) > 0) {
            large = r;
        }

        if (large != root) {
            swap(cells, root, large);
            heapify(cells, size, large);
        }
    }

    private static int compare(Cell first, Cell second) {
        if (first.getY() != second.getY()) {
            return Integer.compare(first.getY(), second.getY());
        }
        return Integer.compare(first.getX(), second.getX());
    }

    private static void swap(Cell[] cells, int first, int second) {
        Cell temporary = cells[first];
        cells[first] = cells[second];
        cells[second] = temporary;
    }
}
