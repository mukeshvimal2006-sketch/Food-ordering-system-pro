package foodorder.ui.components;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Procedurally drawn, flat-design food illustrations used throughout the UI
 * (menu cards, splash screen, login background). No external image files
 * required - everything is rendered with Graphics2D gradients & shapes,
 * giving a clean "app icon" style similar to real food delivery apps.
 */
public class FoodIcon extends JComponent {

    public enum Type { PIZZA, BURGER, DRINK, DESSERT, SALAD, NOODLES, CHICKEN, FRIES, GENERIC }

    private final Type type;
    private double rotation = 0;

    public FoodIcon(Type type, int size) {
        this.type = type;
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    public void setRotation(double radians) {
        this.rotation = radians;
        repaint();
    }

    public double getRotation() {
        return rotation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.translate(w / 2.0, h / 2.0);
        g2.rotate(rotation);
        g2.translate(-w / 2.0, -h / 2.0);

        switch (type) {
            case PIZZA:
                drawPizza(g2, w, h);
                break;
            case BURGER:
                drawBurger(g2, w, h);
                break;
            case DRINK:
                drawDrink(g2, w, h);
                break;
            case DESSERT:
                drawDessert(g2, w, h);
                break;
            case SALAD:
                drawSalad(g2, w, h);
                break;
            case NOODLES:
                drawNoodles(g2, w, h);
                break;
            case CHICKEN:
                drawChicken(g2, w, h);
                break;
            case FRIES:
                drawFries(g2, w, h);
                break;
            default:
                drawGeneric(g2, w, h);
                break;
        }
        g2.dispose();
    }

    private void drawPizza(Graphics2D g2, int w, int h) {
        float pad = w * 0.08f;
        Shape slice = new Ellipse2D.Float(pad, pad, w - 2 * pad, h - 2 * pad);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 213, 79), 0, h, new Color(255, 167, 38)));
        g2.fill(slice);
        g2.setStroke(new BasicStroke(Math.max(2f, w * 0.02f)));
        g2.setColor(new Color(230, 126, 34));
        g2.draw(slice);
        // pepperoni dots
        g2.setColor(new Color(211, 47, 47));
        int[][] dots = {{35, 30}, {60, 22}, {45, 55}, {68, 60}, {28, 65}};
        for (int[] d : dots) {
            float dx = w * d[0] / 100f, dy = h * d[1] / 100f, r = w * 0.07f;
            g2.fill(new Ellipse2D.Float(dx - r / 2, dy - r / 2, r, r));
        }
        // basil flecks
        g2.setColor(new Color(67, 160, 71));
        for (int[] d : new int[][]{{50, 40}, {30, 45}, {60, 70}}) {
            float dx = w * d[0] / 100f, dy = h * d[1] / 100f, r = w * 0.035f;
            g2.fill(new Ellipse2D.Float(dx, dy, r, r));
        }
    }

    private void drawBurger(Graphics2D g2, int w, int h) {
        float cx = w / 2f;
        // bottom bun
        g2.setPaint(new GradientPaint(0, h * 0.7f, new Color(222, 168, 102), 0, h * 0.9f, new Color(193, 138, 76)));
        g2.fill(new RoundRectangle2D.Float(w * 0.1f, h * 0.68f, w * 0.8f, h * 0.16f, w * 0.15f, w * 0.15f));
        // lettuce
        g2.setColor(new Color(124, 179, 66));
        g2.fill(new RoundRectangle2D.Float(w * 0.08f, h * 0.6f, w * 0.84f, h * 0.1f, w * 0.1f, w * 0.1f));
        // patty
        g2.setPaint(new GradientPaint(0, h * 0.48f, new Color(121, 85, 72), 0, h * 0.62f, new Color(78, 52, 46)));
        g2.fill(new RoundRectangle2D.Float(w * 0.1f, h * 0.48f, w * 0.8f, h * 0.14f, w * 0.12f, w * 0.12f));
        // cheese
        g2.setColor(new Color(255, 193, 7));
        Polygon cheese = new Polygon();
        cheese.addPoint((int) (w * 0.12f), (int) (h * 0.50f));
        cheese.addPoint((int) (w * 0.88f), (int) (h * 0.50f));
        cheese.addPoint((int) (w * 0.80f), (int) (h * 0.60f));
        cheese.addPoint((int) (w * 0.20f), (int) (h * 0.60f));
        g2.fill(cheese);
        // top bun
        g2.setPaint(new GradientPaint(0, h * 0.18f, new Color(241, 184, 117), 0, h * 0.48f, new Color(214, 154, 88)));
        g2.fill(new java.awt.geom.Arc2D.Float(w * 0.08f, h * 0.06f, w * 0.84f, h * 0.62f, 0, 180, java.awt.geom.Arc2D.PIE));
        // sesame seeds
        g2.setColor(new Color(255, 248, 225));
        for (int[] d : new int[][]{{35, 22}, {50, 15}, {65, 22}, {42, 30}, {58, 30}}) {
            float dx = w * d[0] / 100f, dy = h * d[1] / 100f, r = w * 0.025f;
            g2.fill(new Ellipse2D.Float(dx, dy, r, r * 1.8f));
        }
    }

    private void drawDrink(Graphics2D g2, int w, int h) {
        float cupTopW = w * 0.55f, cupBotW = w * 0.4f, top = h * 0.18f, bot = h * 0.85f;
        Polygon cup = new Polygon();
        cup.addPoint((int) (w / 2f - cupTopW / 2), (int) top);
        cup.addPoint((int) (w / 2f + cupTopW / 2), (int) top);
        cup.addPoint((int) (w / 2f + cupBotW / 2), (int) bot);
        cup.addPoint((int) (w / 2f - cupBotW / 2), (int) bot);
        g2.setPaint(new GradientPaint(0, top, new Color(129, 212, 250), 0, bot, new Color(79, 195, 247)));
        g2.fill(cup);
        g2.setColor(new Color(2, 119, 189));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(cup);
        // lid
        g2.setColor(new Color(255, 255, 255));
        g2.fill(new RoundRectangle2D.Float(w / 2f - cupTopW / 2 - w * 0.03f, top - h * 0.06f, cupTopW + w * 0.06f, h * 0.08f, 8, 8));
        // straw
        g2.setColor(new Color(229, 57, 53));
        g2.setStroke(new BasicStroke(Math.max(3f, w * 0.045f)));
        g2.drawLine((int) (w * 0.58f), (int) (top - h * 0.18f), (int) (w * 0.52f), (int) (top + h * 0.05f));
    }

    private void drawDessert(Graphics2D g2, int w, int h) {
        // ice cream scoop on cone
        g2.setPaint(new GradientPaint(0, h * 0.15f, new Color(255, 183, 197), 0, h * 0.55f, new Color(244, 143, 177)));
        g2.fill(new Ellipse2D.Float(w * 0.2f, h * 0.12f, w * 0.6f, h * 0.45f));
        Polygon cone = new Polygon();
        cone.addPoint((int) (w * 0.3f), (int) (h * 0.45f));
        cone.addPoint((int) (w * 0.7f), (int) (h * 0.45f));
        cone.addPoint((int) (w * 0.5f), (int) (h * 0.92f));
        g2.setPaint(new GradientPaint(0, h * 0.45f, new Color(255, 183, 77), 0, h * 0.92f, new Color(230, 126, 34)));
        g2.fill(cone);
        g2.setColor(new Color(191, 144, 0));
        for (float yy = 0.5f; yy < 0.9f; yy += 0.12f) {
            g2.drawLine((int) (w * (0.5f - (0.9f - yy) * 0.45f)), (int) (h * yy),
                    (int) (w * (0.5f + (0.9f - yy) * 0.45f)), (int) (h * (yy - 0.06f)));
        }
        g2.setColor(new Color(216, 27, 96));
        g2.fill(new Ellipse2D.Float(w * 0.46f, h * 0.08f, w * 0.08f, w * 0.08f));
    }

    private void drawSalad(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, h * 0.4f, new Color(255, 255, 255), 0, h * 0.85f, new Color(224, 224, 224)));
        g2.fill(new Ellipse2D.Float(w * 0.08f, h * 0.45f, w * 0.84f, h * 0.4f));
        g2.setColor(new Color(189, 189, 189));
        g2.draw(new Ellipse2D.Float(w * 0.08f, h * 0.45f, w * 0.84f, h * 0.4f));
        Color[] veg = {new Color(124, 179, 66), new Color(244, 67, 54), new Color(255, 193, 7), new Color(76, 175, 80)};
        int[][] pts = {{30, 40}, {50, 30}, {65, 42}, {40, 50}, {58, 52}, {45, 38}};
        for (int i = 0; i < pts.length; i++) {
            g2.setColor(veg[i % veg.length]);
            float dx = w * pts[i][0] / 100f, dy = h * pts[i][1] / 100f, r = w * 0.12f;
            g2.fill(new Ellipse2D.Float(dx, dy, r, r * 0.7f));
        }
    }

    private void drawNoodles(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, h * 0.3f, new Color(255, 255, 255), 0, h * 0.85f, new Color(230, 230, 230)));
        g2.fill(new Ellipse2D.Float(w * 0.08f, h * 0.35f, w * 0.84f, h * 0.5f));
        g2.setColor(new Color(255, 202, 40));
        g2.setStroke(new BasicStroke(Math.max(2f, w * 0.03f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 5; i++) {
            int yOff = (int) (h * (0.45f + i * 0.06f));
            g2.drawArc((int) (w * 0.18f), yOff, (int) (w * 0.64f), (int) (h * 0.12f), 0, 180);
        }
        g2.setColor(new Color(67, 160, 71));
        g2.fillOval((int) (w * 0.3f), (int) (h * 0.4f), (int) (w * 0.08f), (int) (h * 0.06f));
        g2.setColor(new Color(229, 57, 53));
        g2.fillOval((int) (w * 0.55f), (int) (h * 0.42f), (int) (w * 0.08f), (int) (h * 0.06f));
    }

    private void drawChicken(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, h * 0.15f, new Color(255, 204, 128), 0, h * 0.7f, new Color(230, 126, 34)));
        g2.fill(new Ellipse2D.Float(w * 0.25f, h * 0.1f, w * 0.5f, h * 0.55f));
        g2.setColor(new Color(255, 255, 255));
        g2.fill(new RoundRectangle2D.Float(w * 0.42f, h * 0.55f, w * 0.16f, h * 0.32f, 10, 10));
        g2.setColor(new Color(238, 238, 238));
        g2.draw(new RoundRectangle2D.Float(w * 0.42f, h * 0.55f, w * 0.16f, h * 0.32f, 10, 10));
    }

    private void drawFries(Graphics2D g2, int w, int h) {
        Polygon box = new Polygon();
        box.addPoint((int) (w * 0.28f), (int) (h * 0.42f));
        box.addPoint((int) (w * 0.72f), (int) (h * 0.42f));
        box.addPoint((int) (w * 0.66f), (int) (h * 0.9f));
        box.addPoint((int) (w * 0.34f), (int) (h * 0.9f));
        g2.setColor(new Color(229, 57, 53));
        g2.fill(box);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine((int) (w * 0.3f), (int) (h * 0.55f), (int) (w * 0.7f), (int) (h * 0.55f));
        g2.setColor(new Color(255, 202, 40));
        for (float xx = 0.32f; xx < 0.68f; xx += 0.09f) {
            g2.fillRoundRect((int) (w * xx), (int) (h * 0.15f), (int) (w * 0.06f), (int) (h * 0.32f), 4, 4);
        }
    }

    private void drawGeneric(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, 0, UITheme_ORANGE_LIGHT(), 0, h, UITheme_ORANGE()));
        g2.fill(new Ellipse2D.Float(w * 0.15f, h * 0.15f, w * 0.7f, h * 0.7f));
        g2.setColor(Color.WHITE);
        g2.setFont(getFont() != null ? getFont().deriveFont(Font.BOLD, w * 0.4f) : new Font("Segoe UI", Font.BOLD, (int) (w * 0.4f)));
        FontMetrics fm = g2.getFontMetrics();
        String s = "?";
        g2.drawString(s, w / 2f - fm.stringWidth(s) / 2f, h / 2f + fm.getAscent() / 2f - 4);
    }

    private static Color UITheme_ORANGE() { return new Color(255, 111, 0); }
    private static Color UITheme_ORANGE_LIGHT() { return new Color(255, 167, 38); }

    /**
     * Returns a real javax.swing.Icon backed by this same FoodIcon drawing logic.
     * Use this (instead of "new FoodIcon(...)") whenever a Swing component asks
     * for an Icon parameter - e.g. new JButton(text, FoodIcon.toIcon(type, 20)).
     * FoodIcon itself is a JComponent and is NOT an Icon, so it cannot be passed
     * directly anywhere an Icon is required.
     */
    public static javax.swing.Icon toIcon(Type type, int size) {
        return new javax.swing.Icon() {
            private final FoodIcon delegate = new FoodIcon(type, size);

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                delegate.setSize(size, size);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(x, y);
                delegate.paint(g2);
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    /** Maps a free-text category name (as typed by Admin) to a representative icon type. */
    public static Type categoryToType(String category) {
        if (category == null) return Type.GENERIC;
        String c = category.toLowerCase();
        if (c.contains("pizza")) return Type.PIZZA;
        if (c.contains("burger")) return Type.BURGER;
        if (c.contains("beverage") || c.contains("drink") || c.contains("juice") || c.contains("coffee") || c.contains("soda")) return Type.DRINK;
        if (c.contains("dessert") || c.contains("sweet") || c.contains("ice")) return Type.DESSERT;
        if (c.contains("salad")) return Type.SALAD;
        if (c.contains("noodle") || c.contains("pasta") || c.contains("biryani") || c.contains("rice")) return Type.NOODLES;
        if (c.contains("chicken") || c.contains("starter") || c.contains("tikka") || c.contains("wing")) return Type.CHICKEN;
        if (c.contains("snack") || c.contains("fries")) return Type.FRIES;
        if (c.contains("main")) return Type.NOODLES;
        return Type.GENERIC;
    }
}
