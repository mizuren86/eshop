USE master;

CREATE DATABASE shopping_website;

USE shopping_website;

CREATE TABLE users(
    user_id INT PRIMARY KEY IDENTITY(1, 1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    [password] VARCHAR(255) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    full_name NVARCHAR(50),
    phone VARCHAR(20),
    user_photo NVARCHAR(255),
    [address] NVARCHAR(255)
);

CREATE TABLE product_category(
    category_id INT PRIMARY KEY IDENTITY(1, 1),
    category_name VARCHAR(255)
);

CREATE TABLE products(
    product_id INT PRIMARY KEY IDENTITY(1, 1),
    sku VARCHAR(255),
    product_name VARCHAR(255),
    [description] VARCHAR(255),
    unit_price DECIMAL(38, 2),
    image_url VARCHAR(255),
    active BIT DEFAULT 1,
    unit_in_stock INT,
    date_create DATETIME DEFAULT GETDATE(),
    last_update DATETIME DEFAULT GETDATE(),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES product_category(category_id)
);



CREATE TABLE shopping_cart(
    cart_id INT PRIMARY KEY IDENTITY(1, 1),
    user_id INT NOT NULL UNIQUE,
    added_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE cart_items(
    cart_item_id INT PRIMARY KEY IDENTITY(1, 1),
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(12, 2),
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(cart_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE orders(
    order_id INT PRIMARY KEY IDENTITY(1, 1),
    user_id INT NOT NULL,
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    payment_method NVARCHAR(50),
    payment_status VARCHAR(50) CHECK(payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded')),
    shipping_address NVARCHAR(255),
    shipping_status VARCHAR(50) CHECK (shipping_status IN ('Processing', 'Shipped', 'Delivered', 'Cancelled')),
    ec_pay_trade_no VARCHAR(30) NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items(
    order_item_id INT PRIMARY KEY IDENTITY(1, 1),
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE payments(
    payment_id INT PRIMARY KEY IDENTITY(1, 1),
    order_id INT NOT NULL UNIQUE,
    user_id INT NOT NULL,
    payment_date DATETIME DEFAULT GETDATE(),
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    payment_method NVARCHAR(50),
    payment_status VARCHAR(50) CHECK (payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded')),
    ec_pay_trade_no VARCHAR(30) NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE shipment(
    shipment_id INT PRIMARY KEY IDENTITY(1, 1),
    order_id INT NOT NULL UNIQUE,
    tracking_number VARCHAR(50) UNIQUE,
    carrier NVARCHAR(50),
    estimated_delivery DATETIME,
    delivery_status VARCHAR(50) CHECK (delivery_status IN ('Pending', 'InTransit', 'Delivered')),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
); 












-- 插入假資料到 users 表
INSERT INTO users (username, [password], email, full_name, phone, user_photo, [address]) VALUES
('alice123', 'password123', 'alice@example.com', 'Alice Johnson', '0912345678', 'alice.jpg', '台北市信義區123號'),
('bob456', 'password456', 'bob@example.com', 'Bob Smith', '0923456789', 'bob.jpg', '台中市南屯區456號'),
('charlie789', 'password789', 'charlie@example.com', 'Charlie Brown', '0934567890', 'charlie.jpg', '高雄市三民區789號'),
('tomlee', 'securepass1', 'tomlee@example.com', 'Tom Lee', '0911222333', 'tomlee.jpg', '台北市中正區100號'),
('janewang', 'securepass2', 'janewang@example.com', 'Jane Wang', '0922333444', 'janewang.jpg', '台南市東區200號');


-- 插入假資料到 product_category 表
INSERT INTO product_category (category_name) VALUES
('電子產品'),
('家居用品'),
('運動器材');


-- 插入假資料到 products 表
INSERT INTO products (sku, product_name, [description], unit_price, image_url, active, unit_in_stock, date_create, last_update, category_id) VALUES
('ELEC001', '藍牙耳機', '高品質無線藍牙耳機', 1999.99, 'bluetooth.jpg', 1, 100, '2025-02-15 10:30:00', '2025-03-10 15:45:00', 1),
('HOME001', '不鏽鋼水壺', '保溫效果佳的不鏽鋼水壺', 599.50, 'bottle.jpg', 1, 200, '2025-02-20 08:15:00', '2025-03-08 12:10:00', 2),
('SPORT001', '瑜伽墊', '環保無毒瑜伽墊', 899.00, 'yoga_mat.jpg', 1, 150, '2025-01-28 14:00:00', '2025-03-05 09:20:00', 3),
('ELEC002', '智慧手環', '多功能健康監測智慧手環', 1299.00, 'smart_band.jpg', 1, 80, '2025-02-25 16:40:00', '2025-03-09 11:30:00', 1),
('HOME002', '電動牙刷', '高速震動清潔電動牙刷', 799.00, 'electric_toothbrush.jpg', 1, 120, '2025-01-18 09:50:00', '2025-03-07 17:00:00', 2);



-- 插入假資料到 shopping_cart 表
INSERT INTO shopping_cart (user_id, added_at) VALUES
(1, '2025-03-01 10:15:00'),
(2, '2025-03-02 14:45:00'),
(3, '2025-03-05 09:30:00'),
(4, '2025-03-07 20:10:00'),
(5, '2025-03-10 16:00:00');



-- 插入假資料到 cart_items 表
INSERT INTO cart_items (cart_id, product_id, quantity, price) VALUES
(1, 1, 2, 1999.99),
(2, 2, 1, 599.50),
(3, 3, 3, 899.00);


-- 插入假資料到 orders 表
INSERT INTO orders (user_id, order_date, total_amount, payment_method, payment_status, shipping_address, shipping_status, ec_pay_trade_no) VALUES
(1, '2025-03-01 11:30:00', 2999.99, 'Credit Card', 'Paid', '台北市中正區忠孝東路100號', 'Shipped', 'TNO202503010001'),
(2, '2025-03-03 15:20:00', 1599.50, 'ATM Transfer', 'Pending', '新北市板橋區文化路200號', 'Processing', 'TNO202503030002'),
(3, '2025-03-05 18:10:00', 899.00, 'Credit Card', 'Paid', '台中市西屯區福星路50號', 'Delivered', 'TNO202503050003'),
(4, '2025-03-08 09:45:00', 4299.00, 'Mobile Payment', 'Paid', '高雄市三民區博愛一路80號', 'Shipped', 'TNO202503080004'),
(5, '2025-03-10 20:30:00', 1299.00, 'Credit Card', 'Failed', '台南市東區東門路150號', 'Cancelled', NULL);




-- 插入假資料到 order_items 表
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 1999.99),
(2, 2, 1, 599.50),
(3, 3, 3, 899.00);


-- 插入假資料到 payments 表
INSERT INTO payments (order_id, user_id, payment_date, amount, payment_method, payment_status, ec_pay_trade_no) VALUES
(1, 1, '2025-03-01 11:45:00', 2999.99, 'Credit Card', 'Paid', 'TNO202503010001'),
(2, 2, '2025-03-03 16:00:00', 1599.50, 'ATM Transfer', 'Pending', 'TNO202503030002'),
(3, 3, '2025-03-05 18:30:00', 899.00, 'Credit Card', 'Paid', 'TNO202503050003'),
(4, 4, '2025-03-08 10:00:00', 4299.00, 'Mobile Payment', 'Paid', 'TNO202503080004'),
(5, 5, '2025-03-10 21:00:00', 1299.00, 'Credit Card', 'Failed', NULL);



-- 插入假資料到 shipment 表
INSERT INTO shipment (order_id, tracking_number, carrier, estimated_delivery, delivery_status) VALUES
(1, 'TRACK123456', 'UPS', '2025-03-15 10:00:00', 'InTransit'),
(2, 'TRACK987654', 'DHL', '2025-03-18 14:00:00', 'Pending'),
(3, 'TRACK112233', 'FedEx', '2025-03-12 09:00:00', 'Delivered');
