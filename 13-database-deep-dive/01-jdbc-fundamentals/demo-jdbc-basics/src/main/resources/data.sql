-- Initial data for JDBC Basics Demo

-- Insert users
INSERT INTO users (name, email, age) VALUES ('John Doe', 'john@example.com', 30);
INSERT INTO users (name, email, age) VALUES ('Jane Smith', 'jane@example.com', 28);
INSERT INTO users (name, email, age) VALUES ('Bob Wilson', 'bob@example.com', 35);
INSERT INTO users (name, email, age) VALUES ('Alice Brown', 'alice@example.com', 25);
INSERT INTO users (name, email, age) VALUES ('Charlie Davis', 'charlie@example.com', NULL);

-- Insert products
INSERT INTO products (name, description, price, quantity, category) 
VALUES ('MacBook Pro', 'Apple laptop with M3 chip', 2499.99, 50, 'Electronics');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('iPhone 15', 'Latest Apple smartphone', 999.99, 100, 'Electronics');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('AirPods Pro', 'Wireless earbuds with ANC', 249.99, 200, 'Electronics');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('Java Programming Book', 'Complete guide to Java', 49.99, 75, 'Books');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('Spring Boot Guide', 'Mastering Spring Boot', 59.99, 60, 'Books');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('Office Chair', 'Ergonomic office chair', 399.99, 30, 'Furniture');

INSERT INTO products (name, description, price, quantity, category) 
VALUES ('Standing Desk', 'Adjustable standing desk', 599.99, 20, 'Furniture');

-- Insert orders
INSERT INTO orders (user_id, status, total_amount) VALUES (1, 'COMPLETED', 3499.98);
INSERT INTO orders (user_id, status, total_amount) VALUES (2, 'PENDING', 999.99);
INSERT INTO orders (user_id, status, total_amount) VALUES (1, 'SHIPPED', 309.98);

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 2499.99);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 2, 1, 999.99);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 2, 1, 999.99);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 3, 1, 249.99);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 4, 1, 59.99);
