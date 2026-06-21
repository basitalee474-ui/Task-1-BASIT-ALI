package numberguess;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumberGameGUI console = new NumberGameGUI();
            console.setVisible(true);
        });
    }
}