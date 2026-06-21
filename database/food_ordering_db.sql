-- =========================================================
-- TastyHub Online Food Ordering System
-- Database Setup Script (MySQL 8+)
-- =========================================================

CREATE DATABASE IF NOT EXISTS food_ordering_db;
USE food_ordering_db;

-- ---------------------------------------------------------
-- Table: users  (both Admin and Customer accounts)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- SHA-256 hashed
    phone VARCHAR(20),
    address VARCHAR(255),
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table: food_items  (menu managed by Admin)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS food_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------
-- Table: orders
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING','CONFIRMED','PREPARING','OUT_FOR_DELIVERY','DELIVERED','CANCELLED')
           NOT NULL DEFAULT 'PENDING',
    delivery_address VARCHAR(255) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Table: order_items  (line items belonging to each order)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    food_id INT NOT NULL,
    food_name VARCHAR(120) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- =========================================================
-- Seed data
-- =========================================================

-- Default admin account -> email: admin@tastyhub.com | password: admin123
-- (password below is the SHA-256 hash of "admin123")
INSERT INTO users (name, email, password, phone, address, role)
VALUES ('System Admin', 'admin@tastyhub.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        '9999999999', 'TastyHub HQ', 'ADMIN')
ON DUPLICATE KEY UPDATE email = email;

-- Sample customer account -> email: john@example.com | password: john123
INSERT INTO users (name, email, password, phone, address, role)
VALUES ('John Carter', 'john@example.com',
        'b4b597c714a8f49103da4dab0266af0ee0ae4f8575250a84855c3d76941cd422',
        '9876543210', '12 Park Avenue, Springfield', 'CUSTOMER')
ON DUPLICATE KEY UPDATE email = email;

-- Sample menu items
INSERT INTO food_items (name, description, price, category, available) VALUES
('Paneer Tikka', 'Char-grilled cottage cheese marinated in spiced yogurt', 180.00, 'Starter', TRUE),
('Chicken Wings', 'Crispy fried wings tossed in tangy hot sauce', 220.00, 'Starter', TRUE),
('Veg Spring Rolls', 'Crunchy rolls stuffed with mixed vegetables', 150.00, 'Starter', TRUE),
('Butter Chicken', 'Creamy tomato-based curry with tender chicken', 320.00, 'Main Course', TRUE),
('Paneer Butter Masala', 'Rich and creamy paneer curry in tomato gravy', 280.00, 'Main Course', TRUE),
('Veg Biryani', 'Fragrant basmati rice cooked with mixed vegetables and spices', 240.00, 'Main Course', TRUE),
('Margherita Pizza', 'Classic pizza with mozzarella and fresh basil', 260.00, 'Main Course', TRUE),
('Gulab Jamun', 'Soft milk dumplings soaked in rose-flavoured sugar syrup', 90.00, 'Dessert', TRUE),
('Chocolate Brownie', 'Warm fudgy brownie served with vanilla ice cream', 140.00, 'Dessert', TRUE),
('Cold Coffee', 'Chilled coffee blended with milk and ice cream', 110.00, 'Beverage', TRUE),
('Fresh Lime Soda', 'Refreshing lime soda, sweet or salted', 70.00, 'Beverage', TRUE),
('French Fries', 'Crispy golden fries served with ketchup', 100.00, 'Snack', TRUE);
