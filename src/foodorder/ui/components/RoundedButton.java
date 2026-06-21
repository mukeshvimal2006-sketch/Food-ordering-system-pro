package foodorder.ui.components;

import foodorder.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A flat, rounded-corner button with an animated hover glow (color eases in/out via a Timer). */
public class RoundedButton extends JButton {

    private final Color baseColor;
    private final Color hoverColor;
    private final Color textColor;
    private float hoverProgress = 0f; // 0 = normal, 1 = fully hovered
    private Timer animTimer;
    private int arc = 14;

    public RoundedButton(String text, Color baseColor, Color hoverColor, Color textColor) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(textColor);
        setFont(UITheme.BODY_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animateTo(1f); }
            @Override public void mouseExited(MouseEvent e) { animateTo(0f); }
        });
    }

    public void setArc(int arc) { this.arc = arc; }

    private void animateTo(float target) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animTimer = new Timer(15, e -> {
            float step = 0.15f;
            if (hoverProgress < target) hoverProgress = Math.min(target, hoverProgress + step);
            else if (hoverProgress > target) hoverProgress = Math.max(target, hoverProgress - step);
            repaint();
            if (hoverProgress == target) ((Timer) e.getSource()).stop();
        });
        animTimer.start();
    }

    private Color lerp(Color a, Color b, float t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = lerp(baseColor, hoverColor, hoverProgress);
        int liftY = (int) (hoverProgress * -2); // subtle lift on hover

        if (hoverProgress > 0.05f) {
            g2.setColor(new Color(0, 0, 0, (int) (40 * hoverProgress)));
            g2.fillRoundRect(2, 4 - liftY, getWidth() - 4, getHeight() - 4, arc, arc);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, liftY, getWidth(), getHeight() - 2, arc, arc);

        g2.dispose();
        setForeground(textColor);
        super.paintComponent(g);
    }
}
