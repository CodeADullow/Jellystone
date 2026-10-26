import java.awt.Color;
import java.awt.Dimension;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

    private Forest forest;
    private JLabel treeCountLabel;
    private JLabel burningCountLabel;
    private JLabel windCountLabel;
    private JLabel positionLabel;
    private JLabel speciesLabel;
    private JLabel stateLabel;

    public ControlPanel(Forest forest) {
        this.forest = forest;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBackground(new Color(235, 235, 235));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addLabel("Forest Fire Simulation");
        add(Box.createVerticalStrut(15));

        Cell[][] grid = forest.getGrid();
        addLabel("Rows: " + grid.length);
        addLabel("Columns: " + grid[0].length);
        positionLabel = addLabel("");
        addLabel("Move: W A S D");
        stateLabel = addLabel("");

        JButton stateButton = new JButton("Pause / Resume");
        stateButton.setAlignmentX(LEFT_ALIGNMENT);
        stateButton.addActionListener(event -> {
            forest.toggleSimulationState();
            refresh();
        });
        add(stateButton);

        add(Box.createVerticalStrut(15));
        addLabel("Live statistics");
        treeCountLabel = addLabel("");
        burningCountLabel = addLabel("");
        windCountLabel = addLabel("");
        speciesLabel = addLabel("");

        Wind wind = forest.getWind();

        if (wind != null) {
            add(Box.createVerticalStrut(15));
            addLabel("Wind");
            addLabel("Direction: " + wind.getDirection());
            addLabel("Speed: " + wind.getSpeed());
            addLabel("Radius: " + wind.getRadius());
        }

        add(Box.createVerticalStrut(15));
        addLabel("Legend");
        addLabel("Tree: green image");
        addLabel("Fire: flame image");
        addLabel("Wind: blue border");

        refresh();
    }

    public void refresh() {
        long trees = forest.countCells(Cell::hasTree);
        long burningTrees = forest.countCells(
                cell -> cell.hasTree() && cell.getTree().isBurning()
        );
        long windAffectedCells = forest.countCells(
                cell -> cell.getWindExposure() > 0.0
        );

        treeCountLabel.setText("Trees: " + trees);
        burningCountLabel.setText("Burning: " + burningTrees);
        windCountLabel.setText("Wind affected: " + windAffectedCells);
        speciesLabel.setText(createSpeciesText());
        stateLabel.setText(
                "State: " + forest.getSimulationStateName()
        );
        positionLabel.setText(
                "Position: " + forest.getOriginX()
                        + ", " + forest.getOriginY()
        );
    }

    private String createSpeciesText() {
        String species = forest.getTreePopulationByType()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("<br>"));

        return "<html>Species:<br>" + species + "</html>";
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5));
        return label;
    }
}
