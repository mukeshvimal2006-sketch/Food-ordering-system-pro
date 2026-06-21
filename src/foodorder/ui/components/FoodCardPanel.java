package foodorder.ui.components;

import foodorder.model.FoodItem;
import foodorder.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * A single animated food menu card: illustrated icon, name, category badge,
 * description, price and an "Add to Cart" button. Lifts and brightens on
 * hover via a smooth Timer-driven animation - similar to real delivery apps.
 */
public class FoodCardPanel extends RoundedCardPanel {

    private float liftProgress = 0f;
    private Timer animTimer;

    public FoodCardPanel(FoodItem item, Consumer<FoodItem> onAddToCart) {
        super(UITheme.SURFACE, 18);
        setShadowSize(7);
        setPreferredSize(new Dimension(240, 290));
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Icon banner
        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(true);
        iconWrap.setBackground(new Color(255, 243, 224));
        iconWrap.setPreferredSize(new Dimension(100, 110));
        iconWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        iconWrap.add(new FoodIcon(FoodIcon.categoryToType(item.getCategory()), 88));
        iconWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        roundBackground(iconWrap, 14);

        JLabel categoryBadge = new JLabel(item.getCategory() == null ? "Food" : item.getCategory().toUpperCase());
        categoryBadge.setFont(UITheme.TINY);
        categoryBadge.setForeground(UITheme.ORANGE_DARK);
        categoryBadge.setOpaque(true);
        categoryBadge.setBackground(new Color(255, 224, 178));
        categoryBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        categoryBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("<html><body style='width:180px'>" + item.getName() + "</body></html>");
        nameLabel.setFont(UITheme.TITLE);
        nameLabel.setForeground(UITheme.INK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String desc = item.getDescription() == null ? "" : item.getDescription();
        if (desc.length() > 70) desc = desc.substring(0, 68) + "...";
        JLabel descLabel = new JLabel("<html><body style='width:190px'>" + desc + "</body></html>");
        descLabel.setFont(UITheme.SMALL);
        descLabel.setForeground(UITheme.INK_SOFT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel priceLabel = new JLabel(String.format("Rs. %.2f", item.getPrice()));
        priceLabel.setFont(UITheme.HEADLINE.deriveFont(17f));
        priceLabel.setForeground(UITheme.ORANGE_DARK);

        RoundedButton addBtn = new RoundedButton("+ Add", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        addBtn.setArc(20);
        addBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        addBtn.addActionListener(e -> {
            if (onAddToCart != null) onAddToCart.accept(item);
            pulse(addBtn);
        });

        bottomRow.add(priceLabel, BorderLayout.WEST);
        bottomRow.add(addBtn, BorderLayout.EAST);

        content.add(iconWrap);
        content.add(Box.createVerticalStrut(10));
        content.add(categoryBadge);
        content.add(Box.createVerticalStrut(6));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(descLabel);
        content.add(Box.createVerticalGlue());
        content.add(bottomRow);

        add(content, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animateLift(1f); }
            @Override public void mouseExited(MouseEvent e) { animateLift(0f); }
        });
    }

    private void roundBackground(JComponent comp, int arc) {
        comp.setOpaque(false);
        comp.setBorder(BorderFactory.createEmptyBorder());
    }

    private void animateLift(float target) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animTimer = new Timer(12, e -> {
            float step = 0.18f;
            if (liftProgress < target) liftProgress = Math.min(target, liftProgress + step);
            else if (liftProgress > target) liftProgress = Math.max(target, liftProgress - step);
            setShadowSize((int) (7 + liftProgress * 5));
            if (liftProgress == target) ((Timer) e.getSource()).stop();
        });
        animTimer.start();
    }

    private void pulse(JComponent comp) {
        Timer[] t = new Timer[1];
        float[] scale = {1f};
        t[0] = new Timer(15, e -> {
            scale[0] += 0.04f;
            if (scale[0] > 1.15f) {
                t[0].stop();
            }
            comp.repaint();
        });
        t[0].start();
    }
}
