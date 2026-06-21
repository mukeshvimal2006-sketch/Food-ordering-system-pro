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

/** Animated registration screen for new customer accounts, matching LoginFrame's visual style. */
public class RegisterFrame extends JFrame {

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;
    private final UserDAO userDAO = new UserDAO();
    private AnimatedGradientPanel backdrop;

    public RegisterFrame() {
        setTitle("TastyHub - Create Account");
        setSize(980, 680);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        backdrop = new AnimatedGradientPanel(UITheme.RED_ACCENT, UITheme.ORANGE);
        backdrop.setLayout(new GridBagLayout());
        setContentPane(backdrop);

        backdrop.add(buildCard());
    }

    private JComponent buildCard() {
        RoundedCardPanel outer = new RoundedCardPanel(UITheme.SURFACE, 26);
        outer.setShadowSize(14);
        outer.setPreferredSize(new Dimension(780, 600));
        outer.setLayout(new GridLayout(1, 2));
        outer.add(buildBrandPanel());
        outer.add(buildFormPanel());
        return outer;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(UITheme.RED_ACCENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 36, 40, 36));

        FoodIcon hero = new FoodIcon(FoodIcon.Type.PIZZA, 130);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("Join TastyHub");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><body style='width:240px'>Create your free account to start ordering from our curated menu in seconds.</body></html>");
        tagline.setFont(UITheme.BODY);
        tagline.setForeground(new Color(255, 230, 228));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(hero);
        panel.add(Box.createVerticalStrut(16));
        panel.add(brand);
        panel.add(Box.createVerticalStrut(8));
        panel.add(tagline);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JScrollPane buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(34, 40, 34, 40));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        int row = 0;

        JLabel title = new JLabel("Create Your Account");
        title.setFont(UITheme.HEADLINE);
        gbc.gridy = row++;
        panel.add(title, gbc);
        gbc.insets = new Insets(0, 0, 14, 0);

        nameField = addLabeledField(panel, gbc, row, "FULL NAME"); row += 2;
        gbc.insets = new Insets(6, 0, 6, 0);
        emailField = addLabeledField(panel, gbc, row, "EMAIL ADDRESS"); row += 2;
        phoneField = addLabeledField(panel, gbc, row, "PHONE NUMBER"); row += 2;

        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(UITheme.TINY);
        passLabel.setForeground(UITheme.INK_SOFT);
        gbc.gridy = row++;
        panel.add(passLabel, gbc);
        passwordField = new JPasswordField();
        styleField(passwordField);
        gbc.gridy = row++;
        panel.add(passwordField, gbc);

        JLabel addrLabel = new JLabel("DELIVERY ADDRESS");
        addrLabel.setFont(UITheme.TINY);
        addrLabel.setForeground(UITheme.INK_SOFT);
        gbc.gridy = row++;
        panel.add(addrLabel, gbc);
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(UITheme.BODY);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setBorder(null);
        gbc.gridy = row++;
        panel.add(addrScroll, gbc);

        RoundedButton registerBtn = new RoundedButton("Register", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        registerBtn.setPreferredSize(new Dimension(0, 46));
        gbc.gridy = row++;
        gbc.insets = new Insets(20, 0, 8, 0);
        panel.add(registerBtn, gbc);
        registerBtn.addActionListener(e -> handleRegister());

        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setFont(UITheme.SMALL);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(UITheme.ORANGE_DARK);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(backBtn, gbc);
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            backdrop.stopAnimation();
            dispose();
        });

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JTextField addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.TINY);
        label.setForeground(UITheme.INK_SOFT);
        gbc.gridy = row;
        panel.add(label, gbc);
        JTextField field = new JTextField();
        styleField(field);
        gbc.gridy = row + 1;
        panel.add(field, gbc);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(UITheme.BODY);
        field.setPreferredSize(new Dimension(0, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String address = addressArea.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "An account with this email already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole("CUSTOMER");

        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            backdrop.stopAnimation();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
