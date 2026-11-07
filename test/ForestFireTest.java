public class ForestFireTest {

    public static void main(String[] args) {
        testRandomFireIgnitesAvailableTree();
        testPeriodicFireEvent();
        testRandomFireHandlesEmptyForest();

        System.out.println("All Forest fire tests passed.");
    }

    private static void testRandomFireIgnitesAvailableTree() {
        Forest forest = createForestWithOneTree();

        check(forest.igniteRandomTree(),
                "A random fire should find an available tree");
        check(forest.hasActiveFire(),
                "The selected tree should be burning");
    }

    private static void testPeriodicFireEvent() {
        Forest forest = createForestWithOneTree();

        for (int update = 0; update < 29; update++) {
            forest.update();
        }

        check(!forest.hasActiveFire(),
                "The periodic fire should wait for thirty updates");

        forest.update();
        check(forest.hasActiveFire(),
                "The thirtieth update should start a random fire");
    }

    private static void testRandomFireHandlesEmptyForest() {
        Forest forest = new Forest(1, 1);
        forest.getGrid()[0][0].removeTree();

        check(!forest.igniteRandomTree(),
                "An empty forest should report that no fire was started");
    }

    private static Forest createForestWithOneTree() {
        Forest forest = new Forest(1, 1);
        forest.getGrid()[0][0].plantTree(new Tree(0.5));
        return forest;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
