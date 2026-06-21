package foodorder.ui;

import foodorder.dao.FoodItemDAO;
import foodorder.dao.OrderDAO;
import foodorder.model.CartItem;
import foodorder.model.FoodItem;
import foodorder.model.Order;
import foodorder.model.User;
import foodorder.ui.components.*;
import foodorder.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Main customer console: animated card-grid menu, live cart badge, toast notifications, checkout, order history. */
public class CustomerDashboardFrame extends JFrame {

    private final User customer;
    private final FoodItemDAO foodItemDAO = new FoodItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final Map<Integer, CartItem> cart = new LinkedHashMap<>();

    private JPanel menuGrid;
    private DefaultTableModel cartTableModel;
    private DefaultTableModel orderTableModel;
    private JLabel cartTotalLabel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton navMenuBtn, navCartBtn, navOrdersBtn;

    public CustomerDashboardFrame(User customer) {
        this.customer = customer;
        setTitle("TastyHub - Welcome " + customer.getName());
        setSize(1180, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKDROP);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSideNav(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(buildMenuScreen(), "MENU");
        contentPanel.add(buildCartScreen(), "CART");
        contentPanel.add(buildOrdersScreen(), "ORDERS");
        add(contentPanel, BorderLayout.CENTER);

        refreshMenuGrid();
        refreshCartTable();
        refreshOrderTable();
        showScreen("MENU");
    }

    // ---------------- Top bar ----------------
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.ORANGE);
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(new FoodIcon(FoodIcon.Type.PIZZA, 36));
        JLabel brand = new JLabel("TastyHub");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        left.add(brand);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);
        JLabel welcome = new JLabel("Hi, " + customer.getName());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(UITheme.BODY_BOLD);
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

    // ---------------- Side navigation ----------------
    private JPanel buildSideNav() {
        JPanel nav = new JPanel();
        nav.setBackground(UITheme.INK);
        nav.setPreferredSize(new Dimension(190, 0));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        navMenuBtn = navButton("Browse Menu", FoodIcon.Type.BURGER);
        navCartBtn = navButtonWithBadge("My Cart");
        navOrdersBtn = navButton("My Orders", FoodIcon.Type.NOODLES);

        navMenuBtn.addActionListener(e -> showScreen("MENU"));
        navCartBtn.addActionListener(e -> showScreen("CART"));
        navOrdersBtn.addActionListener(e -> showScreen("ORDERS"));

        nav.add(navMenuBtn);
        nav.add(Box.createVerticalStrut(4));
        nav.add(navCartBtn);
        nav.add(Box.createVerticalStrut(4));
        nav.add(navOrdersBtn);
        nav.add(Box.createVerticalGlue());

        return nav;
    }

    private JButton navButton(String text, FoodIcon.Type type) {
        JButton btn = new JButton(text, FoodIcon.toIcon(type, 20));
        styleNavButton(btn);
        return btn;
    }

    private JButton navButtonWithBadge(String text) {
        JButton btn = new JButton(text, FoodIcon.toIcon(FoodIcon.Type.FRIES, 20));
        styleNavButton(btn);
        return btn;
    }

    private void styleNavButton(JButton btn) {
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(14);
        btn.setForeground(new Color(220, 220, 230));
        btn.setFont(UITheme.BODY_BOLD);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 16));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(190, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addChangeListener(e -> {
            if (btn.getModel().isRollover()) {
                btn.setOpaque(true);
                btn.setBackground(new Color(255, 255, 255, 20));
            } else {
                btn.setOpaque(false);
            }
        });
    }

    private void showScreen(String name) {
        cardLayout.show(contentPanel, name);
        Color active = new Color(255, 255, 255, 30);
        navMenuBtn.setOpaque("MENU".equals(name));
        navCartBtn.setOpaque("CART".equals(name));
        navOrdersBtn.setOpaque("ORDERS".equals(name));
        navMenuBtn.setBackground(active);
        navCartBtn.setBackground(active);
        navOrdersBtn.setBackground(active);
        if ("ORDERS".equals(name)) refreshOrderTable();
    }

    // ---------------- Menu screen (animated card grid) ----------------
    private JPanel buildMenuScreen() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Our Menu");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        JLabel sub = new JLabel("Freshly prepared, ready to deliver");
        sub.setFont(UITheme.SMALL);
        sub.setForeground(UITheme.INK_SOFT);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshMenuGrid());
        header.add(refreshBtn, BorderLayout.EAST);

        wrapper.add(header, BorderLayout.NORTH);

        menuGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
        menuGrid.setOpaque(false);

        JScrollPane scroll = new JScrollPane(menuGrid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private void refreshMenuGrid() {
        menuGrid.removeAll();
        for (FoodItem item : foodItemDAO.getAvailableFoodItems()) {
            menuGrid.add(new FoodCardPanel(item, this::addToCart));
        }
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private void addToCart(FoodItem item) {
        CartItem existing = cart.get(item.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            cart.put(item.getId(), new CartItem(item.getId(), item.getName(), item.getPrice(), 1));
        }
        refreshCartTable();
        ToastNotification.show(this, item.getName() + " added to cart", UITheme.GREEN_ACCENT);
    }

    // ---------------- Cart screen ----------------
    private JPanel buildCartScreen() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("My Cart");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        wrapper.add(title, BorderLayout.NORTH);

        cartTableModel = new DefaultTableModel(new Object[]{"ID", "Item", "Price", "Quantity", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(UITheme.BODY);
        cartTable.getTableHeader().setFont(UITheme.BODY_BOLD);

        RoundedCardPanel tableCard = new RoundedCardPanel(UITheme.SURFACE, 16);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        wrapper.add(tableCard, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton updateQtyBtn = new JButton("Update Quantity");
        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear Cart");
        actions.add(updateQtyBtn);
        actions.add(removeBtn);
        actions.add(clearBtn);

        cartTotalLabel = new JLabel("Total: Rs. 0.00");
        cartTotalLabel.setFont(UITheme.HEADLINE);
        cartTotalLabel.setForeground(UITheme.ORANGE_DARK);

        RoundedButton checkoutBtn = new RoundedButton("Proceed to Checkout", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        checkoutBtn.setPreferredSize(new Dimension(230, 46));

        JPanel totalsRow = new JPanel(new BorderLayout());
        totalsRow.setOpaque(false);
        totalsRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        totalsRow.add(cartTotalLabel, BorderLayout.WEST);
        totalsRow.add(checkoutBtn, BorderLayout.EAST);

        south.add(actions, BorderLayout.NORTH);
        south.add(totalsRow, BorderLayout.SOUTH);
        wrapper.add(south, BorderLayout.SOUTH);

        updateQtyBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a cart item first."); return; }
            int foodId = (int) cartTableModel.getValueAt(row, 0);
            try {
                int newQty = Integer.parseInt(cartTableModel.getValueAt(row, 3).toString());
                if (newQty <= 0) cart.remove(foodId);
                else cart.get(foodId).setQuantity(newQty);
                refreshCartTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            }
        });

        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a cart item first."); return; }
            int foodId = (int) cartTableModel.getValueAt(row, 0);
            cart.remove(foodId);
            refreshCartTable();
        });

        clearBtn.addActionListener(e -> { cart.clear(); refreshCartTable(); });

        checkoutBtn.addActionListener(e -> openCheckoutDialog());

        return wrapper;
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (CartItem ci : cart.values()) {
            cartTableModel.addRow(new Object[]{
                    ci.getFoodId(), ci.getFoodName(), String.format("Rs. %.2f", ci.getPrice()),
                    ci.getQuantity(), String.format("Rs. %.2f", ci.getSubtotal())
            });
            total += ci.getSubtotal();
        }
        if (cartTotalLabel != null) cartTotalLabel.setText(String.format("Total: Rs. %.2f", total));
        if (navCartBtn != null) {
            navCartBtn.setText(cart.isEmpty() ? "My Cart" : "My Cart (" + cart.size() + ")");
        }
    }

    private void openCheckoutDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add items from the menu first.");
            return;
        }

        JDialog dialog = new JDialog(this, "Checkout", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.SURFACE);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 24, 4, 24);

        double total = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();

        FoodIcon checkIcon = new FoodIcon(FoodIcon.Type.DESSERT, 70);
        JPanel iconWrap = new JPanel();
        iconWrap.setOpaque(false);
        iconWrap.add(checkIcon);

        JLabel totalLabel = new JLabel(String.format("Order Total: Rs. %.2f", total));
        totalLabel.setFont(UITheme.HEADLINE);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea addressArea = new JTextArea(customer.getAddress(), 3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1, true));

        JComboBox<String> paymentBox = new JComboBox<>(new String[]{"Cash on Delivery", "Credit/Debit Card", "UPI", "Net Banking"});

        int row = 0;
        gbc.gridy = row++; dialog.add(iconWrap, gbc);
        gbc.gridy = row++; dialog.add(totalLabel, gbc);
        gbc.gridy = row++; gbc.insets = new Insets(16, 24, 4, 24); dialog.add(new JLabel("Delivery Address"), gbc);
        gbc.gridy = row++; gbc.insets = new Insets(0, 24, 4, 24); dialog.add(new JScrollPane(addressArea), gbc);
        gbc.gridy = row++; gbc.insets = new Insets(10, 24, 4, 24); dialog.add(new JLabel("Payment Method"), gbc);
        gbc.gridy = row++; gbc.insets = new Insets(0, 24, 4, 24); dialog.add(paymentBox, gbc);

        RoundedButton placeOrderBtn = new RoundedButton("Place Order", UITheme.ORANGE, UITheme.ORANGE_DARK, Color.WHITE);
        placeOrderBtn.setPreferredSize(new Dimension(0, 46));
        gbc.gridy = row++; gbc.insets = new Insets(20, 24, 16, 24);
        dialog.add(placeOrderBtn, gbc);

        placeOrderBtn.addActionListener(e -> {
            String address = addressArea.getText().trim();
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Delivery address is required.");
                return;
            }
            String payment = (String) paymentBox.getSelectedItem();
            int orderId = orderDAO.placeOrder(customer.getId(), customer.getName(),
                    new java.util.ArrayList<>(cart.values()), address, payment);

            if (orderId > 0) {
                cart.clear();
                refreshCartTable();
                refreshOrderTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "Order #" + orderId + " placed successfully!\nThank you for ordering with TastyHub.",
                        "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
                showScreen("ORDERS");
            } else {
                JOptionPane.showMessageDialog(dialog, "Could not place order. Please try again.");
            }
        });

        dialog.setVisible(true);
    }

    // ---------------- Orders screen ----------------
    private JPanel buildOrdersScreen() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("My Orders");
        title.setFont(UITheme.DISPLAY.deriveFont(24f));
        title.setForeground(UITheme.INK);
        wrapper.add(title, BorderLayout.NORTH);

        orderTableModel = new DefaultTableModel(
                new Object[]{"Order ID", "Total", "Status", "Payment", "Date", "Delivery Address"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(30);
        orderTable.setFont(UITheme.BODY);
        orderTable.getTableHeader().setFont(UITheme.BODY_BOLD);

        RoundedCardPanel tableCard = new RoundedCardPanel(UITheme.SURFACE, 16);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        wrapper.add(tableCard, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshOrderTable());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        toolbar.add(refreshBtn);
        wrapper.add(toolbar, BorderLayout.SOUTH);

        return wrapper;
    }

    private void refreshOrderTable() {
        if (orderTableModel == null) return;
        orderTableModel.setRowCount(0);
        for (Order o : orderDAO.getOrdersByUser(customer.getId())) {
            orderTableModel.addRow(new Object[]{
                    o.getId(), String.format("Rs. %.2f", o.getTotalAmount()), o.getStatus(),
                    o.getPaymentMethod(), o.getOrderDate(), o.getDeliveryAddress()
            });
        }
    }
}
