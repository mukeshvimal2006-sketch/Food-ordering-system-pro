package foodorder.ui.components;

import javax.swing.*;
import java.awt.*;

/** A small circular counter badge that "pops" with a bounce animation whenever its count changes. */
public class CartBadge extends JComponent {

    private int count = 0;
    private float scale = 1f;
    private Timer bounceTimer;

    public CartBadge() {
        setPreferredSize(new Dimension(22, 22));
        setOpaque(false);
    }

    public void setCount(int count) {
        boolean increased = count > this.count;
        this.count = count;
        if (increased) bounce();
        repaint();
    }

    private void bounce() {
        if (bounceTimer != null && bounceTimer.isRunning()) bounceTimer.stop();
        scale = 1.5f;
        bounceTimer = new Timer(20, e -> {
            scale -= 0.06f;
            if (scale <= 1f) {
                scale = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        bounceTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (count <= 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int base = Math.min(getWidth(), getHeight());
        int size = (int) (base * scale);
        int offset = (base - size) / 2;

        g2.setColor(new Color(229, 57, 53));
        g2.fillOval(offset, offset, size, size);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(9, (int) (size * 0.5))));
        String text = count > 9 ? "9+" : String.valueOf(count);
        FontMetrics fm = g2.getFontMetrics();
        int tx = offset + (size - fm.stringWidth(text)) / 2;
        int ty = offset + (size + fm.getAscent()) / 2 - 2;
        g2.drawString(text, tx, ty);
        g2.dispose();
    }
}
