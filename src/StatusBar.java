import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {

    private JLabel label;
    private final String defaultString = "Ready";
    private String current;

    public StatusBar(int width) {
        super.setPreferredSize(new Dimension (width, 24));
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        current = " " + defaultString;
        label = new JLabel(current);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        this.add(label);
    }
    public void setStatus(String status) {
        current = " " + status;
        label.setText(current);
    }

    public String getStatus() {
        return current;
    }

    public void setDefault() {
        current = " "  + defaultString;
        label.setText(current);
    }
}
