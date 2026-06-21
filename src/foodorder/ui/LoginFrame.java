package foodorder.ui;

import foodorder.dao.UserDAO;
import foodorder.model.User;
import foodorder.ui.components.AnimatedGradientPanel;
import foodorder.ui.components.FoodIcon;
import foodorder.ui.components.RoundedButton;
import foodorder.ui.components.RoundedCardPanel;
import foodorder.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/** Animated login screen: warm gradient backdrop with drifting food icons, glass-style login card, fade-in entrance. */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private final UserDAO userDAO = new UserDAO();
    private AnimatedGradientPanel backdrop;

    public LoginFrame() {
        setTitle("TastyHub - Online Food Ordering System | Login");
        setSize(960, 600);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        backdrop = new AnimatedGradientPanel(UITheme.ORANGE, UITheme.RED_ACCENT);
        backdrop.setLayout(new GridBagLayout());
        setContentPane(backdrop);

        backdrop.add(buildSplitCard());
    }

    private JComponent buildSplitCard() {
        RoundedCardPanel outer = new RoundedCardPanel(UITheme.SURFACE, 26);
        outer.setShadowSize(14);
        outer.setPreferredSize(new Dimension(760, 460));
        outer.setLayout(new GridLayout(1, 2));

        outer.add(buildBrandPanel());
        outer.add(buildFormPanel());
        return outer;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(UITheme.ORANGE_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 36, 40, 36));

        FoodIcon hero = new FoodIcon(FoodIcon.Type.BURGER, 130);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("TastyHub");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><body style='width:240px'>Delicious food, delivered fast. Order from a curated menu and track it live, right to your door.</body></html>");
        tagline.setFont(UITheme.BODY);
        tagline.setForeground(new Color(255, 235, 220));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel iconsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        iconsRow.setOpaque(false);
        iconsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconsRow.add(new FoodIcon(FoodIcon.Type.PIZZA, 36));
        iconsRow.add(new FoodIcon(FoodIcon.Type.DRINK, 36));
        iconsRow.add(new FoodIcon(FoodIcon.Type.DESSERT, 36));
        iconsRow.add(new FoodIcon(FoodIcon.Type.NOODLES, 36));

        panel.add(hero);
        panel.add(Box.createVerticalStrut(16));
        panel.add(brand);
        panel.add(Box.createVerticalStrut(8));
        panel.add(tagline);
        panel.add(Box.createVerticalGlue());
        panel.add(iconsRow);

        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new GridBagLayout());
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.gridx = 0;
        igbc.fill = GridBagConstraints.HORIZONTAL;
        igbc.insets = new Insets(7, 0, 7, 0);

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(UITheme.HEADLINE);
        welcome.setForeground(UITheme.INK);
        igbc.gridy = 0;
        inner.add(welcome, igbc);

        JLabel sub = new JLabel("Login to continue ordering delicious food");
        sub.setFont(UITheme.SMALL);
        sub.setForeground(UITheme.INK_SOFT);
        igbc.gridy = 1;
        igbc.insets = new Insets(0, 0, 16, 0);
        inner.add(sub, igbc);

        JLabel emailLabel = new JLabel("EMAIL ADDRESS");
        emailLabel.setFont(UITheme.TINY);
        emailLabel.setForeground(UITheme.INK_SOFT);
        igbc.gridy = 2;
        igbc.insets = new Insets(7, 0, 4, 0);
        inner.add(emailLabel, igbc);

        emailField = styledField();
        igbc.gridy = 3;
        inner.add(emailField, igbc);

        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(UITheme.TINY);
        passLabel.setForeground(UITheme.INK_SOFT);
        igbc.gridy = 4;
        igbc.insets = new Insets(10, 0, 4, 0);
        inner.add(passLabel, igbc);

        passwordField = new JPasswordField();
        styleField(passwordField);
        igbc.gridy = 5;
        igbc.insets = new Insets(0, 0, 7, 0);
        inner.add(passwordField, igbc);

        RoundedButton loginBtn = new RoundedButton("Login", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(0, 46));
        igbc.gridy = 6;
        igbc.insets = new Insets(18, 0, 8, 0);
        inner.add(loginBtn, igbc);
        loginBtn.addActionListener(this::handleLogin);
        getRootPane().setDefaultButton(loginBtn);

        JButton registerLink = new JButton("Create a new account");
        registerLink.setFont(UITheme.SMALL);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(UITheme.ORANGE_DARK);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        igbc.gridy = 7;
        igbc.insets = new Insets(2, 0, 0, 0);
        inner.add(registerLink, igbc);
        registerLink.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            backdrop.stopAnimation();
            dispose();
        });

        JLabel demo = new JLabel("<html><center>Admin demo: admin@tastyhub.com / admin123</center></html>");
        demo.setFont(UITheme.TINY);
        demo.setForeground(UITheme.INK_SOFT);
        demo.setHorizontalAlignment(SwingConstants.CENTER);
        igbc.gridy = 8;
        igbc.insets = new Insets(18, 0, 0, 0);
        inner.add(demo, igbc);

        gbc.gridy = 0;
        panel.add(inner, gbc);
        return panel;
    }

    private JTextField styledField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(UITheme.BODY);
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        backdrop.stopAnimation();
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            new AdminDashboardFrame(user).setVisible(true);
        } else {
            new CustomerDashboardFrame(user).setVisible(true);
        }
        dispose();
    }

    public static void main(String[] args) {
        foodorder.util.UIHelper.applyNimbus();
        SwingUtilities.invokeLater(() -> new SplashScreen().setVisible(true));
    }
}
