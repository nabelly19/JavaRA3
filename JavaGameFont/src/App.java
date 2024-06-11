import javax.swing.*;
public class App{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CampoMinado frame = new CampoMinado();
            frame.setVisible(true);
        });
    }}