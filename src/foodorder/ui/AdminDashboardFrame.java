package foodorder.ui;

import foodorder.dao.FoodItemDAO;
import foodorder.dao.OrderDAO;
import foodorder.dao.UserDAO;
import foodorder.model.FoodItem;
import foodorder.model.Order;
import foodorder.model.OrderItem;
import foodorder.model.User;
import foodorder.ui.components.FoodIcon;
import foodorder.ui.components.RoundedButton;
import foodorder.ui.components.RoundedCardPanel;
import foodorder.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Main admin console: animated stat dashboard, menu management, order management, customer list. */
public class AdminDashboardFrame extends JFrame {

    private final User admin;
    private final FoodItemDAO foodItemDAO = new FoodItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    private JLabel statOrders, statPending, statRevenue, statItems;
    private DefaultTableModel foodTableModel;
    private JTable foodTable;
    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    private DefaultTableModel customerTableModel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton[] navButtons;

    public AdminDashboardFrame(User admin) {
        this.admin = admin;
        setTitle("TastyHub Admin Console - " + admin.getName());
        setSize(1200, 740);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKDROP);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSideNav(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(buildDashboardTab(), "DASH");
        contentPanel.add(buildMenuTab(), "MENU");
        contentPanel.add(buildOrdersTab(), "ORDERS");
        contentPanel.add(buildCustomersTab(), "CUSTOMERS");
        add(contentPanel, BorderLayout.CENTER);

        refreshDashboard();
        refreshFoodTable();
        refreshOrderTable();
        refreshCustomerTable();
        showScreen("DASH");
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.INK);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(new FoodIcon(FoodIcon.Type.CHICKEN, 32));
        JLabel brand = new JLabel("TastyHub Admin");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(Color.WHITE);
        left.add(brand);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, " + admin.getName());
        welcome.setForeground(new Color(200, 200, 215));
        RoundedButton logoutBtn = new RoundedButton("Logout", UITheme.RED_ACCENT, new Color(183, 28, 28), Color.WHITE);
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        right.add(welcome);
        right.add(logoutBtn);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSideNav() {
        JPanel nav = new JPanel();
        nav.setBackground(new Color(26, 26, 33));
        nav.setPreferredSize(new Dimension(200, 0));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        String[] labels = {"Dashboard", "Manage Menu", "Manage Orders", "Customers"};
        FoodIcon.Type[] icons = {FoodIcon.Type.PIZZA, FoodIcon.Type.BURGER, FoodIcon.Type.NOODLES, FoodIcon.Type.SALAD};
        String[] keys = {"DASH", "MENU", "ORDERS", "CUSTOMERS"};
        navButtons = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i], FoodIcon.toIcon(icons[i], 20));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(14);
            btn.setForeground(new Color(220, 220, 230));
            btn.setFont(UITheme.BODY_BOLD);
            btn.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 16));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 50));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String key = keys[i];
            btn.addActionListener(e -> showScreen(key));
            navButtons[i] = btn;
            nav.add(btn);
            nav.add(Box.createVerticalStrut(4));
        }
        nav.add(Box.createVerticalGlue());
        return nav;
    }

    private void showScreen(String key) {
        cardLayout.show(contentPanel, key);
        String[] keys = {"DASH", "MENU", "ORDERS", "CUSTOMERS"};
        for (int i = 0; i < keys.length; i++) {
            boolean active = keys[i].equals(key);
            navButtons[i].setOpaque(active);
            navButtons[i].setBackground(new Color(255, 111, 0, 50));
        }
        if ("DASH".equals(key)) refreshDashboard();
        if ("ORDERS".equals(key)) refreshOrderTable();
        if ("CUSTOMERS".equals(key)) refreshCustomerTable();
    }

    // ---------------- Dashboard tab ----------------
    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        panel.add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 18, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        statOrders = new JLabel("0");
        statPending = new JLabel("0");
        statRevenue = new JLabel("Rs. 0.00");
        statItems = new JLabel("0");

        statsPanel.add(statCard("Total Orders", statOrders, FoodIcon.Type.NOODLES, UITheme.ORANGE));
        statsPanel.add(statCard("Pending Orders", statPending, FoodIcon.Type.FRIES, UITheme.YELLOW_ACCENT));
        statsPanel.add(statCard("Total Revenue", statRevenue, FoodIcon.Type.BURGER, UITheme.GREEN_ACCENT));
        statsPanel.add(statCard("Menu Items", statItems, FoodIcon.Type.PIZZA, UITheme.RED_ACCENT));

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(statsPanel, BorderLayout.NORTH);
        panel.add(centerWrap, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Stats");
        refreshBtn.addActionListener(e -> refreshDashboard());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setOpaque(false);
        south.add(refreshBtn);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private RoundedCardPanel statCard(String titleText, JLabel valueLabel, FoodIcon.Type type, Color accent) {
        RoundedCardPanel card = new RoundedCardPanel(UITheme.SURFACE, 16);
        card.setShadowSize(8);
        card.setPreferredSize(new Dimension(0, 130));
        card.setLayout(new BorderLayout());

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel iconStripe = new JPanel(new BorderLayout());
        iconStripe.setOpaque(false);
        FoodIcon icon = new FoodIcon(type, 36);
        iconStripe.add(icon, BorderLayout.EAST);

        JLabel titleLabel = new JLabel(titleText.toUpperCase());
        titleLabel.setFont(UITheme.TINY);
        titleLabel.setForeground(UITheme.INK_SOFT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent.darker());

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(6));
        textBox.add(valueLabel);

        inner.add(textBox, BorderLayout.WEST);
        inner.add(iconStripe, BorderLayout.EAST);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboard() {
        List<Order> orders = orderDAO.getAllOrders();
        long pending = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        statOrders.setText(String.valueOf(orders.size()));
        statPending.setText(String.valueOf(pending));
        statRevenue.setText(String.format("Rs. %.2f", orderDAO.getTotalRevenue()));
        statItems.setText(String.valueOf(foodItemDAO.getAllFoodItems().size()));
    }

    // ---------------- Menu management tab ----------------
    private JPanel buildMenuTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Manage Menu");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        panel.add(title, BorderLayout.NORTH);

        foodTableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price", "Available", "Description"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        foodTable = new JTable(foodTableModel);
        foodTable.setRowHeight(28);
        foodTable.setFont(UITheme.BODY);
        foodTable.getTableHeader().setReorderingAllowed(false);
        foodTable.getTableHeader().setFont(UITheme.BODY_BOLD);

        RoundedCardPanel tableCard = new RoundedCardPanel(UITheme.SURFACE, 16);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(foodTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        toolbar.setOpaque(false);
        RoundedButton addBtn = new RoundedButton("+ Add New Item", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> openFoodDialog(null));
        editBtn.addActionListener(e -> {
            int row = foodTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an item to edit."); return; }
            int id = (int) foodTableModel.getValueAt(row, 0);
            openFoodDialog(foodItemDAO.getFoodItemById(id));
        });
        deleteBtn.addActionListener(e -> {
            int row = foodTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an item to delete."); return; }
            int id = (int) foodTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this food item permanently?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                foodItemDAO.deleteFoodItem(id);
                refreshFoodTable();
                refreshDashboard();
            }
        });
        refreshBtn.addActionListener(e -> refreshFoodTable());

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void openFoodDialog(FoodItem existing) {
        JDialog dialog = new JDialog(this, existing == null ? "Add New Food Item" : "Edit Food Item", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.SURFACE);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 20, 4, 20);

        JTextField nameField = new JTextField(existing != null ? existing.getName() : "");
        JTextField priceField = new JTextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Starter", "Main Course", "Dessert", "Beverage", "Snack"});
        if (existing != null) categoryBox.setSelectedItem(existing.getCategory());
        JTextArea descArea = new JTextArea(existing != null ? existing.getDescription() : "", 3, 20);
        descArea.setLineWrap(true);
        JCheckBox availableBox = new JCheckBox("Available for ordering", existing == null || existing.isAvailable());

        FoodIcon previewIcon = new FoodIcon(FoodIcon.categoryToType((String) categoryBox.getSelectedItem()), 70);
        JPanel previewWrap = new JPanel();
        previewWrap.setOpaque(false);
        previewWrap.add(previewIcon);
        categoryBox.addActionListener(e -> {
            previewWrap.removeAll();
            previewWrap.add(new FoodIcon(FoodIcon.categoryToType((String) categoryBox.getSelectedItem()), 70));
            previewWrap.revalidate();
            previewWrap.repaint();
        });

        int row = 0;
        gbc.gridy = row++; dialog.add(previewWrap, gbc);
        gbc.gridy = row++; dialog.add(new JLabel("Item Name"), gbc);
        gbc.gridy = row++; dialog.add(nameField, gbc);
        gbc.gridy = row++; dialog.add(new JLabel("Price (Rs.)"), gbc);
        gbc.gridy = row++; dialog.add(priceField, gbc);
        gbc.gridy = row++; dialog.add(new JLabel("Category"), gbc);
        gbc.gridy = row++; dialog.add(categoryBox, gbc);
        gbc.gridy = row++; dialog.add(new JLabel("Description"), gbc);
        gbc.gridy = row++; dialog.add(new JScrollPane(descArea), gbc);
        gbc.gridy = row++; dialog.add(availableBox, gbc);

        RoundedButton saveBtn = new RoundedButton(existing == null ? "Add Item" : "Save Changes", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        gbc.gridy = row++; gbc.insets = new Insets(16, 20, 8, 20);
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            if (name.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and price are required.");
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price must be a valid number.");
                return;
            }

            FoodItem item = existing != null ? existing : new FoodItem();
            item.setName(name);
            item.setPrice(price);
            item.setCategory((String) categoryBox.getSelectedItem());
            item.setDescription(descArea.getText().trim());
            item.setAvailable(availableBox.isSelected());

            boolean success = existing != null ? foodItemDAO.updateFoodItem(item) : foodItemDAO.addFoodItem(item);
            if (success) {
                refreshFoodTable();
                refreshDashboard();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Operation failed. Check your database connection.");
            }
        });

        dialog.setVisible(true);
    }

    private void refreshFoodTable() {
        foodTableModel.setRowCount(0);
        for (FoodItem item : foodItemDAO.getAllFoodItems()) {
            foodTableModel.addRow(new Object[]{
                    item.getId(), item.getName(), item.getCategory(),
                    String.format("Rs. %.2f", item.getPrice()),
                    item.isAvailable() ? "Yes" : "No",
                    item.getDescription()
            });
        }
    }

    // ---------------- Orders management tab ----------------
    private JPanel buildOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Manage Orders");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        panel.add(title, BorderLayout.NORTH);

        orderTableModel = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Total", "Status", "Payment", "Date", "Address"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(28);
        orderTable.setFont(UITheme.BODY);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.getTableHeader().setFont(UITheme.BODY_BOLD);

        RoundedCardPanel tableCard = new RoundedCardPanel(UITheme.SURFACE, 16);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        toolbar.setOpaque(false);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "PENDING", "CONFIRMED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"});
        RoundedButton updateBtn = new RoundedButton("Update Status", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        JButton viewItemsBtn = new JButton("View Order Items");
        JButton refreshBtn = new JButton("Refresh");

        updateBtn.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an order first."); return; }
            int orderId = (int) orderTableModel.getValueAt(row, 0);
            String status = (String) statusBox.getSelectedItem();
            orderDAO.updateOrderStatus(orderId, status);
            refreshOrderTable();
            refreshDashboard();
        });

        viewItemsBtn.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select an order first."); return; }
            int orderId = (int) orderTableModel.getValueAt(row, 0);
            showOrderItemsDialog(orderId);
        });

        refreshBtn.addActionListener(e -> refreshOrderTable());

        toolbar.add(new JLabel("New Status:"));
        toolbar.add(statusBox);
        toolbar.add(updateBtn);
        toolbar.add(viewItemsBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void showOrderItemsDialog(int orderId) {
        List<OrderItem> items = orderDAO.getOrderItems(orderId);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Food Item", "Price", "Qty", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        double total = 0;
        for (OrderItem oi : items) {
            model.addRow(new Object[]{oi.getFoodName(), String.format("Rs. %.2f", oi.getPrice()),
                    oi.getQuantity(), String.format("Rs. %.2f", oi.getSubtotal())});
            total += oi.getSubtotal();
        }
        JTable table = new JTable(model);
        JDialog dialog = new JDialog(this, "Order #" + orderId + " - Items", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel totalLabel = new JLabel("  Order Total: Rs. " + String.format("%.2f", total));
        totalLabel.setFont(UITheme.BODY_BOLD);
        dialog.add(totalLabel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void refreshOrderTable() {
        if (orderTableModel == null) return;
        orderTableModel.setRowCount(0);
        for (Order o : orderDAO.getAllOrders()) {
            orderTableModel.addRow(new Object[]{
                    o.getId(), o.getCustomerName(), String.format("Rs. %.2f", o.getTotalAmount()),
                    o.getStatus(), o.getPaymentMethod(), o.getOrderDate(), o.getDeliveryAddress()
            });
        }
    }

    // ---------------- Customers tab ----------------
    private JPanel buildCustomersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Customers");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        panel.add(title, BorderLayout.NORTH);

        customerTableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Email", "Phone", "Address"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(28);
        customerTable.setFont(UITheme.BODY);
        customerTable.getTableHeader().setFont(UITheme.BODY_BOLD);

        RoundedCardPanel tableCard = new RoundedCardPanel(UITheme.SURFACE, 16);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshCustomerTable());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        toolbar.setOpaque(false);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshCustomerTable() {
        if (customerTableModel == null) return;
        customerTableModel.setRowCount(0);
        for (User u : userDAO.getAllCustomers()) {
            customerTableModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getAddress()});
        }
    }
}
