import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Forest forest = new Forest(40, 60);
        Random random = new Random();

        Wind wind = new Wind(30, "EAST", 0, 0, 15);
        forest.setWind(wind);
        wind.randomize(forest, random);


        IgnitionSource source;
        Lightning lightning = null;

        int choice = random.nextInt(3);

        if (choice == 0) {
            lightning = new Lightning();
            source = lightning;
            System.out.println("Ignition source: Lightning");

        } else if (choice == 1) {
            source = new Arson(20, 20);
            System.out.println("Ignition source: Arson");

        } else {
            source = new Backburning(20);
            System.out.println("Ignition source: Backburning");
        }

        try {
            source.ignite(forest);
        }
        catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Ignition failed: " + e.getMessage());
        }


        JFrame window = new JFrame("Forest Fire Simulation");

        ForestPanel panel = new ForestPanel(forest, lightning);
        ControlPanel controlPanel = new ControlPanel(forest);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.add(
                controlPanel,
                BorderLayout.EAST
        );
        contentPanel.add(
                controlPanel.getWindPanel(),
                BorderLayout.SOUTH
        );

        window.setContentPane(contentPanel);

        window.setSize(900, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Timer timer = new Timer(500, event -> {

            forest.update();

            panel.repaint();
            controlPanel.refresh();

        });

        timer.start();
    }
}

// DONT CHANGE THE CODE BELOW. IDEAS from Week 10 & 11
//
// For boundless grid, keep track of not only trees but a biome or group of trees; in memory block, when move back down and then move back up, create biome based on a number tag made


//
// Colour map can be used for statistics; represents biomes, trees, fires and wind.


//
// Collect data from trees


//
//



// Filter which fire group has a strong centerpoint (which firecell has more surrounding firecells) and a weaker centerpoint (random shortest firecell).



//
// Type down coordinates of a cell, highlight a cell and provide statistics for it in the panel
