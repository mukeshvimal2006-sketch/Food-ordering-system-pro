package foodorder.ui.components;

import javax.swing.*;
import java.awt.*;

/** A simple rounded, drop-shadowed card container. Children are added via getContentPanel(). */
public class RoundedCardPanel extends JPanel {

    private final int arc;
    private final Color bg;
    private int shadowSize = 6;

    public RoundedCardPanel(Color bg, int arc) {
        this.bg = bg;
        this.arc = arc;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize + 4, shadowSize));
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize + 4, shadowSize));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        // shadow (soft, layered)
        for (int i = shadowSize; i > 0; i--) {
            g2.setColor(new Color(33, 33, 41, Math.max(2, 10 - i)));
            g2.fillRoundRect(shadowSize - i, shadowSize - i + 3, w - 2 * (shadowSize - i), h - 2 * (shadowSize - i), arc, arc);
        }

        g2.setColor(bg);
        g2.fillRoundRect(shadowSize, shadowSize, w - 2 * shadowSize, h - 2 * shadowSize - 4, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
