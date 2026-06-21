package foodorder.ui.components;

import javax.swing.*;
import java.awt.*;

/** A simple rotating-arc loading spinner, animated via a Swing Timer. */
public class SpinnerIcon extends JComponent {

    private int angle = 0;
    private final Color color;
    private Timer timer;

    public SpinnerIcon(int size, Color color) {
        this.color = color;
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    public void start() {
        if (timer != null) return;
        timer = new Timer(16, e -> {
            angle = (angle + 8) % 360;
            repaint();
        });
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int stroke = Math.max(3, getWidth() / 10);
        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        g2.drawOval(stroke, stroke, getWidth() - 2 * stroke, getHeight() - 2 * stroke);
        g2.setColor(color);
        g2.drawArc(stroke, stroke, getWidth() - 2 * stroke, getHeight() - 2 * stroke, angle, 100);
        g2.dispose();
    }
}
