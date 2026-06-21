package foodorder.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A full-bleed animated background panel: a slowly shifting two-tone gradient
 * plus several softly bobbing/rotating translucent food icons drifting in the
 * backdrop - gives the login/splash screens a lively, "real app" feel without
 * needing any external image/video assets.
 */
public class AnimatedGradientPanel extends JPanel {

    private float phase = 0f;
    private final Color colorA;
    private final Color colorB;
    private final List<FloatingIcon> floaters = new ArrayList<>();
    private Timer timer;

    private static class FloatingIcon {
        FoodIcon.Type type;
        float x, y;       // 0..1 relative position
        float baseY;
        float size;
        float speed;
        float rotSpeed;
        float rot = 0;
        FloatingIcon(FoodIcon.Type type, float x, float y, float size, float speed, float rotSpeed) {
            this.type = type; this.x = x; this.y = y; this.baseY = y;
            this.size = size; this.speed = speed; this.rotSpeed = rotSpeed;
        }
    }

    public AnimatedGradientPanel(Color colorA, Color colorB) {
        this.colorA = colorA;
        this.colorB = colorB;
        setOpaque(true);

        floaters.add(new FloatingIcon(FoodIcon.Type.PIZZA, 0.08f, 0.18f, 90, 1.0f, 0.004f));
        floaters.add(new FloatingIcon(FoodIcon.Type.BURGER, 0.85f, 0.15f, 80, 1.3f, -0.005f));
        floaters.add(new FloatingIcon(FoodIcon.Type.DRINK, 0.90f, 0.65f, 70, 0.8f, 0.003f));
        floaters.add(new FloatingIcon(FoodIcon.Type.DESSERT, 0.05f, 0.7f, 75, 1.1f, -0.004f));
        floaters.add(new FloatingIcon(FoodIcon.Type.FRIES, 0.5f, 0.9f, 60, 0.9f, 0.006f));
        floaters.add(new FloatingIcon(FoodIcon.Type.NOODLES, 0.92f, 0.92f, 65, 1.2f, -0.003f));

        timer = new Timer(40, e -> {
            phase += 0.015f;
            for (FloatingIcon f : floaters) {
                f.y = f.baseY + 0.02f * (float) Math.sin(phase * f.speed * 2 * Math.PI / 4);
                f.rot += f.rotSpeed;
            }
            repaint();
        });
        timer.start();
    }

    public void stopAnimation() {
        if (timer != null) timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        float shift = (float) (0.15 * Math.sin(phase));
        GradientPaint gp = new GradientPaint(
                0, 0, colorA,
                w * (0.8f + shift), h, colorB);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // soft decorative circles
        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillOval((int) (w * 0.7f), (int) (-h * 0.1f), (int) (w * 0.5f), (int) (w * 0.5f));
        g2.setColor(new Color(255, 255, 255, 12));
        g2.fillOval((int) (-w * 0.15f), (int) (h * 0.6f), (int) (w * 0.45f), (int) (w * 0.45f));

        for (FloatingIcon f : floaters) {
            Graphics2D ig = (Graphics2D) g2.create();
            ig.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
            float px = f.x * w - f.size / 2;
            float py = f.y * h - f.size / 2;
            ig.translate(px, py);
            ig.translate(f.size / 2.0, f.size / 2.0);
            ig.rotate(f.rot);
            ig.translate(-f.size / 2.0, -f.size / 2.0);
            FoodIcon icon = new FoodIcon(f.type, (int) f.size);
            icon.setSize((int) f.size, (int) f.size);
            icon.paint(ig);
            ig.dispose();
        }
        g2.dispose();
    }
}
