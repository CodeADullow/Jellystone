import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

    public ControlPanel(Forest forest) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBackground(new Color(235, 235, 235));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addLabel("Forest Fire Simulation");
        add(Box.createVerticalStrut(15));

        Cell[][] grid = forest.getGrid();
        addLabel("Rows: " + grid.length);
        addLabel("Columns: " + grid[0].length);

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
    }

    private void addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5));
    }
}
