import java.util.Random;

// responsible for Tree objects; based off a cool presentation idea in Week 13 regarding species selector/generator.
public class TreeFactory {

    public enum Species {OAK, EUCALYPTUS, BIRCH, SPRUCE, JUNGLE}
    private Random random;

    public TreeFactory(Random random) {
        if (random == null) {
            throw new IllegalArgumentException(
                    "Random generator must not be null"
            );
        }

        this.random = random;
    }

    public Tree create(Species species, double spreadability) {
        if (species == null) {
            throw new IllegalArgumentException("Species must not be null");
        }

        switch (species) {
            case OAK:
                return new Oak(spreadability);
            case EUCALYPTUS:
                return new Eucalyptus(spreadability);
            case BIRCH:
                return new Birch(spreadability);
            case SPRUCE:
                return new Spruce(spreadability);
            case JUNGLE:
                return new Jungle(spreadability);
            default:
                throw new IllegalArgumentException(
                        "Unsupported species: " + species
                );
        }
    }

    public Tree createRandom(double spreadability) {
        Species[] species = Species.values();
        Species selectedSpecies = species[random.nextInt(species.length)];
        return create(selectedSpecies, spreadability);
    }
}
