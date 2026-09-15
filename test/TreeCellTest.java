public class TreeCellTest {

    public static void main(String[] args) {
        testDefaultTreeType();
        testSubclassTreeTypes();
        testBurning();
        testWindIncreasesBurnRate();
        testDamageAndDeath();
        testCellRemoval();
        testValidation();

        System.out.println("All Tree/Cell tests passed.");
    }

    private static void testDefaultTreeType() {
        Tree tree = new Tree(0.5);

        check(
                "OAK".equals(tree.getTreeType()),
                "Default Tree should report OAK"
        );

        check(
                tree.getHealth() == tree.getMaxHealth(),
                "New tree should start at full health"
        );

        check(
                tree.isAlive(),
                "New tree should be alive"
        );
    }

    private static void testSubclassTreeTypes() {
        Tree oak = new Oak(0.5);
        Tree birch = new Birch(0.5);
        Tree eucalyptus = new Eucalyptus(0.5);
        Tree spruce = new Spruce(0.5);
        Tree jungle = new Jungle(0.5);

        check(
                "OAK".equals(oak.getTreeType()),
                "Oak should report OAK"
        );

        check(
                "BIRCH".equals(birch.getTreeType()),
                "Birch should report BIRCH"
        );

        check(
                "EUCALYPTUS".equals(eucalyptus.getTreeType()),
                "Eucalyptus should report EUCALYPTUS"
        );

        check(
                "SPRUCE".equals(spruce.getTreeType()),
                "Spruce should report SPRUCE"
        );

        check(
                "JUNGLE".equals(jungle.getTreeType()),
                "Jungle should report JUNGLE"
        );
    }

    private static void testBurning() {
        Tree tree = new Eucalyptus(0.5);

        check(
                !tree.isBurning(),
                "New tree should not be burning"
        );

        tree.ignite();

        check(
                tree.isBurning(),
                "ignite() should start burning"
        );

        check(
                tree.getBurnIntensity() > 0.0,
                "Burning tree should have burn intensity"
        );

        double healthBefore = tree.getHealth();

        tree.advanceBurning();

        check(
                tree.getHealth() < healthBefore,
                "Burning should reduce health"
        );
    }

    private static void testDamageAndDeath() {
        Tree tree = new Oak(0.5);

        tree.damage(tree.getMaxHealth());

        check(
                !tree.isAlive(),
                "Tree should die at zero health"
        );

        check(
                !tree.isBurning(),
                "Dead tree should not be burning"
        );

        tree.ignite();

        check(
                !tree.isBurning(),
                "Dead tree should not be able to ignite"
        );
    }

    private static void testWindIncreasesBurnRate() {
        Forest calmForest = new Forest(1, 1);
        Tree calmTree = new Tree(0.5);
        calmForest.getGrid()[0][0].plantTree(calmTree);
        calmTree.ignite();
        calmForest.update();

        Forest windyForest = new Forest(1, 1);
        Tree windyTree = new Tree(0.5);
        windyForest.getGrid()[0][0].plantTree(windyTree);
        windyForest.setWind(new Wind(100.0, "EAST", 0, 0, 0));
        windyTree.ignite();
        windyForest.update();

        check(
                windyTree.getHealth() < calmTree.getHealth(),
                "Wind-exposed trees should burn faster"
        );
    }

    private static void testCellRemoval() {
        Cell cell = new Cell(0, 0);
        Tree tree = new Birch(0.5);

        cell.plantTree(tree);

        check(
                cell.hasTree(),
                "Cell should contain planted tree"
        );

        Tree removed = cell.removeTree();

        check(
                removed == tree,
                "removeTree() should return removed tree"
        );

        check(
                cell.isEmpty(),
                "Cell should be empty after removal"
        );
    }

    private static void testValidation() {
        boolean exceptionThrown = false;

        try {
            new Tree(-0.1);
        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }

        check(
                exceptionThrown,
                "Negative spreadability should be rejected"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
