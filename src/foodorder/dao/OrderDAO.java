package foodorder.dao;

import foodorder.model.CartItem;
import foodorder.model.Order;
import foodorder.model.OrderItem;
import foodorder.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /** Places an order transactionally: inserts the order header plus every line item. Returns the new order id, or -1 on failure. */
    public int placeOrder(int userId, String customerName, List<CartItem> cartItems,
                           String deliveryAddress, String paymentMethod) {
        String orderSql = "INSERT INTO orders (user_id, customer_name, total_amount, status, delivery_address, payment_method, order_date) " +
                "VALUES (?, ?, ?, 'PENDING', ?, ?, NOW())";
        String itemSql = "INSERT INTO order_items (order_id, food_id, food_name, price, quantity) VALUES (?, ?, ?, ?, ?)";

        double total = 0;
        for (CartItem c : cartItems) total += c.getSubtotal();

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int orderId;
            try (PreparedStatement ps = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, customerName);
                ps.setDouble(3, total);
                ps.setString(4, deliveryAddress);
                ps.setString(5, paymentMethod);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                for (CartItem c : cartItems) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, c.getFoodId());
                    ps.setString(3, c.getFoodName());
                    ps.setDouble(4, c.getPrice());
                    ps.setInt(5, c.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return orderId;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return -1;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapOrder(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setId(rs.getInt("id"));
                    oi.setOrderId(rs.getInt("order_id"));
                    oi.setFoodId(rs.getInt("food_id"));
                    oi.setFoodName(rs.getString("food_name"));
                    oi.setPrice(rs.getDouble("price"));
                    oi.setQuantity(rs.getInt("quantity"));
                    list.add(oi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS revenue FROM orders WHERE status <> 'CANCELLED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("revenue");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setDeliveryAddress(rs.getString("delivery_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        return o;
    }
}
