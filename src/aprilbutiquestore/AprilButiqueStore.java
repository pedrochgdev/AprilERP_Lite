package aprilbutiquestore;

import javax.swing.SwingUtilities;

public class AprilButiqueStore {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LogIn login = new LogIn();
            login.setVisible(true);
        });
    }
}