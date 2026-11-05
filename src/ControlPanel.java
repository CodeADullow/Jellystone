import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ControlPanel extends JPanel {

    private Forest forest;
    private JLabel treeCountLabel;
    private JLabel burningCountLabel;
    private JLabel safeCountLabel;
    private JLabel fireIntensityLabel;
    private JLabel windCountLabel;
    private JLabel positionLabel;
    private JLabel speciesLabel;
    private JLabel healthLabel;
    private JLabel stateLabel;
    private JPanel windPanel;
    private JLabel windStartLabel;
    private JLabel windDirectionLabel;
    private JLabel windSpeedLabel;
    private JLabel windRadiusLabel;
    private JTextField extraWindCellsInput;
    private JLabel extraWindCellsMessage;
    private Runnable resetAction;

    public ControlPanel(Forest forest) {
        this(forest, forest::reset);
    }

    public ControlPanel(Forest forest, Runnable resetAction) {
        this.forest = forest;
        this.resetAction = resetAction;

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

        JButton resetButton = new JButton("Reset");
        resetButton.setAlignmentX(LEFT_ALIGNMENT);
        resetButton.addActionListener(event -> {
            resetAction.run();
            refresh();
        });
        add(resetButton);

        add(Box.createVerticalStrut(15));
        addLabel("Live statistics");
        treeCountLabel = addLabel("");
        burningCountLabel = addLabel("");
        safeCountLabel = addLabel("");
        fireIntensityLabel = addLabel("");
        windCountLabel = addLabel("");
        speciesLabel = addLabel("");
        healthLabel = addLabel("");

        add(Box.createVerticalStrut(15));
        addLabel("Legend");
        addLabel("Tree: green image");
        addLabel("Fire: flame image");
        addLabel("Wind: blue border");

        windPanel = createWindPanel();
        refresh();
    }

    public JPanel getWindPanel() {
        return windPanel;
    }

    public void refresh() {
        long trees = forest.countCells(Cell::hasTree);
        long windAffectedCells = forest.countCells(
                cell -> cell.getWindExposure() > 0.0
        );
        Map<Boolean, Long> treeStates =
                forest.getTreeStateCounts();

        treeCountLabel.setText("Trees: " + trees);
        burningCountLabel.setText(
                "Burning: " + treeStates.get(true)
        );
        safeCountLabel.setText(
                "Not burning: " + treeStates.get(false)
        );
        fireIntensityLabel.setText(String.format(
                "Fire intensity: %.2f",
                forest.getTotalFireIntensity()
        ));
        windCountLabel.setText("Wind affected: " + windAffectedCells);
        speciesLabel.setText(createSpeciesText());
        healthLabel.setText(createHealthText());
        stateLabel.setText(
                "State: " + forest.getSimulationStateName()
        );
        positionLabel.setText(
                "Position: " + forest.getOriginX()
                        + ", " + forest.getOriginY()
        );

        Wind wind = forest.getWind();
        if (wind != null && windStartLabel != null) {
            windDirectionLabel.setText(
                    "Direction: " + wind.getDirection()
            );
            windSpeedLabel.setText("Speed: " + wind.getSpeed());
            windRadiusLabel.setText("Radius: " + wind.getRadius());
            windStartLabel.setText(
                    "Wind cells: " + wind.getWindCellCount()
                            + " | First: " + wind.getStartX()
                            + ", " + wind.getStartY()
            );
        }
    }

    private String createSpeciesText() {
        String species = forest.getTreePopulationByType()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("<br>"));

        return "<html>Species:<br>" + species + "</html>";
    }

    private String createHealthText() {
        String health = forest.getTreeHealthByType()
                .entrySet()
                .stream()
                .map(entry -> String.format(
                        "%s: %.1f%%",
                        entry.getKey(),
                        entry.getValue().getAverage()
                ))
                .collect(Collectors.joining("<br>"));

        return "<html>Average health:<br>" + health + "</html>";
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5));
        return label;
    }

    private JPanel createWindPanel() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 25, 8)
        );
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBackground(new Color(220, 240, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Wind"));

        Wind wind = forest.getWind();
        if (wind == null) {
            panel.add(new JLabel("No active wind"));
        } else {
            windDirectionLabel = new JLabel();
            windSpeedLabel = new JLabel();
            windRadiusLabel = new JLabel();
            windStartLabel = new JLabel();

            panel.add(windDirectionLabel);
            panel.add(windSpeedLabel);
            panel.add(windRadiusLabel);
            panel.add(windStartLabel);

            panel.add(new JLabel("Extra wind cells:"));
            extraWindCellsInput = new JTextField("0", 4);
            panel.add(extraWindCellsInput);

            JButton setBonusButton = new JButton("Set");
            setBonusButton.addActionListener(event -> setExtraWindCells());
            panel.add(setBonusButton);

            extraWindCellsMessage = new JLabel();
            panel.add(extraWindCellsMessage);
        }

        return panel;
    }

    private void setExtraWindCells() {
        try {
            int additionalCells = Integer.parseInt(
                    extraWindCellsInput.getText().trim()
            );
            forest.getWind().setAdditionalWindCells(additionalCells);
            extraWindCellsMessage.setText("Applied next randomisation");
            refresh();
        } catch (IllegalArgumentException exception) {
            extraWindCellsMessage.setText("Enter 0 or more");
        }
    }
}
