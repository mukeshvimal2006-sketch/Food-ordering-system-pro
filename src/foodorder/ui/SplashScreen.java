package foodorder.ui;

import foodorder.ui.components.FoodIcon;
import foodorder.ui.components.SpinnerIcon;
import foodorder.util.UITheme;

import javax.swing.*;
import java.awt.*;

/** Animated splash/loading screen shown briefly when the application starts. */
public class SplashScreen extends JWindow {

    private final String[] messages = {
            "Preheating the ovens...",
            "Tossing fresh ingredients...",
            "Plating your experience...",
            "Almost ready to serve..."
    };

    public SplashScreen() {
        setSize(440, 320);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.ORANGE);
        root.setBorder(BorderFactory.createLineBorder(UITheme.ORANGE_DARK, 2));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        FoodIcon logo = new FoodIcon(FoodIcon.Type.PIZZA, 100);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("TastyHub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Online Food Ordering System");
        subtitle.setFont(UITheme.BODY);
        subtitle.setForeground(new Color(255, 245, 235));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        SpinnerIcon spinner = new SpinnerIcon(38, Color.WHITE);
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinner.start();

        JLabel statusLabel = new JLabel(messages[0]);
        statusLabel.setFont(UITheme.SMALL);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(255, 255, 255, 80));
        progressBar.setForeground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(280, 8));
        progressBar.setPreferredSize(new Dimension(280, 8));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(logo);
        center.add(Box.createVerticalStrut(10));
        center.add(title);
        center.add(subtitle);
        center.add(Box.createVerticalStrut(22));
        center.add(spinner);
        center.add(Box.createVerticalStrut(16));
        center.add(progressBar);
        center.add(Box.createVerticalStrut(10));
        center.add(statusLabel);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // Rotate the logo gently
        Timer rotateTimer = new Timer(40, e -> logo.setRotation(logo.getRotation() + 0.04));
        rotateTimer.start();

        // Progress + status cycling, then launch LoginFrame
        Timer progressTimer = new Timer(45, null);
        int[] progress = {0};
        progressTimer.addActionListener(e -> {
            progress[0] += 2;
            progressBar.setValue(Math.min(100, progress[0]));
            int msgIndex = Math.min(messages.length - 1, progress[0] / (100 / messages.length));
            statusLabel.setText(messages[msgIndex]);
            if (progress[0] >= 100) {
                progressTimer.stop();
                rotateTimer.stop();
                spinner.stop();
                SwingUtilities.invokeLater(() -> {
                    new LoginFrame().setVisible(true);
                    dispose();
                });
            }
        });
        progressTimer.start();
    }

    public static void main(String[] args) {
        foodorder.util.UIHelper.applyNimbus();
        SwingUtilities.invokeLater(() -> new SplashScreen().setVisible(true));
    }
}
