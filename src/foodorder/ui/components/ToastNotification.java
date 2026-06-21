package foodorder.ui.components;

import foodorder.util.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * A small slide-in notification card that appears in the bottom-right corner
 * of a frame's layered pane, then auto-dismisses after a short delay -
 * mimics the "Added to cart!" style toasts seen in real food delivery apps.
 */
public class ToastNotification {

    public static void show(JFrame frame, String message, Color accent) {
        JLayeredPane layeredPane = frame.getLayeredPane();

        JPanel toast = new RoundedCardPanel(UITheme.INK, 14);
        toast.setLayout(new BorderLayout());
        JLabel label = new JLabel("  " + message + "  ");
        label.setForeground(Color.WHITE);
        label.setFont(UITheme.BODY_BOLD);
        label.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(6, 10));

        toast.add(stripe, BorderLayout.WEST);
        toast.add(label, BorderLayout.CENTER);

        int width = 260;
        int height = 50;
        int frameW = frame.getWidth();
        int frameH = frame.getHeight();
        int startX = frameW;
        int targetX = frameW - width - 24;
        int y = frameH - height - 30;

        toast.setBounds(startX, y, width, height);
        layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        final int[] x = {startX};
        Timer slideIn = new Timer(10, null);
        slideIn.addActionListener(e -> {
            x[0] -= (x[0] - targetX) * 0.25 + 2;
            if (x[0] <= targetX) {
                x[0] = targetX;
                slideIn.stop();
                // hold, then slide out
                Timer hold = new Timer(1400, ev -> slideOut(layeredPane, toast, targetX, y, width, height, frameW));
                hold.setRepeats(false);
                hold.start();
            }
            toast.setBounds(x[0], y, width, height);
        });
        slideIn.start();
    }

    private static void slideOut(JLayeredPane layeredPane, JPanel toast, int startX, int y, int width, int height, int frameW) {
        final int[] x = {startX};
        Timer slideOut = new Timer(10, null);
        slideOut.addActionListener(e -> {
            x[0] += (frameW - x[0]) * 0.25 + 3;
            toast.setBounds(x[0], y, width, height);
            if (x[0] >= frameW) {
                ((Timer) e.getSource()).stop();
                layeredPane.remove(toast);
                layeredPane.repaint();
            }
        });
        slideOut.start();
    }
}
