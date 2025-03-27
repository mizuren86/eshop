USE master;

/* 刪除shopping_website資料庫指令
USE master ;  
GO  
DROP DATABASE shopping_website

USE master;
ALTER DATABASE shopping_website SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE shopping_website;
*/

CREATE DATABASE shopping_website;

USE shopping_website;

<<<<<<< HEAD:db-scripts/eeit147-5thSQL-ver2.45.sql
-- 删除已存在的约束
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UQ__users__email]') AND type in (N'UQ'))
BEGIN
    ALTER TABLE [dbo].[users] DROP CONSTRAINT [UQ__users__email]
END

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[UQ__users__username]') AND type in (N'UQ'))
BEGIN
    ALTER TABLE [dbo].[users] DROP CONSTRAINT [UQ__users__username]
END

CREATE TABLE users(
    user_id INT PRIMARY KEY IDENTITY(1, 1),
    username VARCHAR(255) NOT NULL,
    [password] VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(255),
    user_photo VARCHAR(255),
    [address] VARCHAR(255)
=======
CREATE TABLE users(
    user_id INT PRIMARY KEY IDENTITY(1, 1),
    username VARCHAR(255) UNIQUE NOT NULL,
    [password] VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(255),
    user_photo NVARCHAR(255),
    [address] NVARCHAR(255),
    avatar_path VARCHAR(255),
    last_login_time DATETIME2(6),
    account_status VARCHAR(255),
    vip_level INT,
    vip_points INT
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac:db-scripts/eeit147-5thSQL-ver2.43.txt
);

-- 添加唯一约束
ALTER TABLE users ADD CONSTRAINT UQ__users__username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT UQ__users__email UNIQUE (email);

CREATE TABLE product_category(
    category_id INT PRIMARY KEY IDENTITY(1, 1),
    category_name VARCHAR(255)
);

CREATE TABLE products(
    product_id INT PRIMARY KEY IDENTITY(1, 1),
    category_id INT NOT NULL,
    sku VARCHAR(255),
    product_name VARCHAR(255),
    [description] VARCHAR(255),
    unit_price DECIMAL(10, 2),
    image_url VARCHAR(255),
    active BIT DEFAULT 1,
    unit_in_stock INT,
    date_create DATETIME DEFAULT GETDATE(),
    last_update DATETIME DEFAULT GETDATE(),
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
    price DECIMAL(10, 2),
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(cart_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE orders(
    order_id INT PRIMARY KEY IDENTITY(1, 1),
    user_id INT NOT NULL,
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    payment_method VARCHAR(255),
    payment_status VARCHAR(255) CHECK(payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded')),
    shipping_address NVARCHAR(255),
    shipping_status VARCHAR(255) CHECK (shipping_status IN ('Processing', 'Shipped', 'Delivered', 'Cancelled')),
    ec_pay_trade_no VARCHAR(255),
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
    payment_method VARCHAR(255),
    payment_status VARCHAR(255) CHECK (payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded')),
    ec_pay_trade_no VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE shipment(
    shipment_id INT PRIMARY KEY IDENTITY(1, 1),
    order_id INT NOT NULL UNIQUE,
    tracking_number VARCHAR(255),
    carrier VARCHAR(255),
    estimated_delivery DATETIME,
    delivery_status VARCHAR(255) CHECK (delivery_status IN ('Pending', 'InTransit', 'Delivered')),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE chat_sessions(
    chat_id INT IDENTITY(1,1) PRIMARY KEY,
    chat_user_id INT NOT NULL,
    support_id INT NULL,
    Status NVARCHAR(20) CHECK (Status IN ('Open', 'Closed')) DEFAULT 'Open',
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    closed_at DATETIME NULL,
    FOREIGN KEY (chat_user_id) REFERENCES users(user_id),
    FOREIGN KEY (support_id) REFERENCES users(user_id)
);

CREATE TABLE messages(
    message_id INT IDENTITY(1,1) PRIMARY KEY,
    chat_id INT NOT NULL,
    sender_id INT NOT NULL,
    message_text NVARCHAR(MAX) NOT NULL,
    is_ai_response BIT DEFAULT 0,
    sent_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (chat_id) REFERENCES chat_sessions(chat_id),
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
);

CREATE TABLE faqs(
    faq_id INT IDENTITY(1,1) PRIMARY KEY,
    question NVARCHAR(500) NOT NULL,
    answer NVARCHAR(1000) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE feedback(
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    feedback_user_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    comment NVARCHAR(1000),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (feedback_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

<<<<<<< HEAD:db-scripts/eeit147-5thSQL-ver2.45.sql
CREATE TABLE [dbo].[coupon](
	[coupon_id] [int] IDENTITY(1,1) NOT NULL,
	[coupon_code] [varchar](50) NOT NULL,
	[target_amount] [int] NOT NULL,
	[discount_amount] [int] NOT NULL,
	[end_date] [datetime] NOT NULL,
=======
CREATE TABLE user_vip(
    vip_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    is_vip BIT NOT NULL DEFAULT 1,
    vip_level INT,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    vip_photo VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac:db-scripts/eeit147-5thSQL-ver2.43.txt
);

CREATE TABLE user_vip_history(
    history_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    vip_level INT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    vip_photo VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE reviews(
    review_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    reviews_product_id INT NOT NULL,
    reviews_user_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    comment NVARCHAR(1000),
    photo VARBINARY(MAX),
    updated_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Product FOREIGN KEY (reviews_product_id) REFERENCES products(product_id),
    CONSTRAINT FK_User FOREIGN KEY (reviews_user_id) REFERENCES users(user_id),
    CONSTRAINT UQ_user_product_review UNIQUE (reviews_user_id, reviews_product_id)
);

CREATE TABLE coupon(
    coupon_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    users_id INT NOT NULL,
    coupon_discount INT NOT NULL,
    coupon_date_timeout DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (users_id) REFERENCES users(user_id)
);

CREATE TABLE email_verification(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    created_at DATETIME2(6),
    email VARCHAR(255) NOT NULL,
    expires_at DATETIME2(6) NOT NULL,
    is_verified BIT,
    token VARCHAR(255) NOT NULL
);

CREATE TABLE password_reset_tokens(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    expiry_date DATETIME2(6),
    token VARCHAR(255),
    used BIT NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE roles(
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    description VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE role_permissions(
    role_id INT NOT NULL,
    permission VARCHAR(255),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE user_roles(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE user_activity(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    activity_time DATETIME2(6) NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(64),
    status VARCHAR(20),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE user_operation_log(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    create_time DATETIME2(6) NOT NULL,
    description VARCHAR(255),
    execution_time BIGINT,
    ip VARCHAR(64) NOT NULL,
    method VARCHAR(255) NOT NULL,
    operation VARCHAR(100) NOT NULL,
    params TEXT NOT NULL,
    username VARCHAR(50) NOT NULL
);

-- 创建索引
CREATE UNIQUE NONCLUSTERED INDEX [UKla2ts67g4oh2sreayswhox1i6] ON [dbo].[password_reset_tokens]([user_id]);
CREATE NONCLUSTERED INDEX [idx_activity_time] ON [dbo].[user_activity]([activity_time]);
CREATE NONCLUSTERED INDEX [idx_activity_type] ON [dbo].[user_activity]([activity_type]);
CREATE NONCLUSTERED INDEX [idx_user_id] ON [dbo].[user_activity]([user_id]);
CREATE NONCLUSTERED INDEX [idx_create_time] ON [dbo].[user_operation_log]([create_time]);
CREATE NONCLUSTERED INDEX [idx_username] ON [dbo].[user_operation_log]([username]);

-- 插入假資料到 users 表
INSERT INTO users (username, [password], email, full_name, phone, user_photo, [address]) VALUES
('alice123', 'password123', 'alice@example.com', 'Alice Johnson', '0912345678', 'alice.jpg', '台北市信義區123號'),
('bob456', 'password456', 'bob@example.com', 'Bob Smith', '0923456789', 'bob.jpg', '台中市南屯區456號'),
('charlie789', 'password789', 'charlie@example.com', 'Charlie Brown', '0934567890', 'charlie.jpg', '高雄市三民區789號'),
('tomlee', 'securepass1', 'tomlee@example.com', 'Tom Lee', '0911222333', 'tomlee.jpg', '台北市中正區100號'),
('janewang', 'securepass2', 'janewang@example.com', 'Jane Wang', '0922333444', 'janewang.jpg', '台南市東區200號');

-- 插入商品分類資料
INSERT INTO product_category (category_name)
VALUES ('水果'), ('熱帶水果'), ('柑橘類水果');


-- 插入水果商品資料，圖片路徑指向本機專案的靜態資源
INSERT INTO products (sku, product_name, [description], unit_price, image_url, unit_in_stock, category_id)
VALUES 
<<<<<<< HEAD:db-scripts/eeit147-5thSQL-ver2.45.sql
('FRU0001', '蘋果', '新鮮紅蘋果', 99.99, '/img/fruite-item-1.jpg', 100, 1),
('FRU0002', '香蕉', '新鮮黃香蕉', 49.99, '/img/fruite-item-2.jpg', 150, 2),
('FRU0003', '橙子', '新鮮橙子', 79.99, '/img/fruite-item-3.jpg', 120, 3),
('FRU0004', '葡萄', '新鮮紫葡萄', 149.99, '/img/fruite-item-4.jpg', 80, 1),
('FRU0005', '鳳梨', '新鮮甜鳳梨', 119.99, '/img/fruite-item-5.jpg', 90, 2),
('FRU0006', '西瓜', '新鮮西瓜', 199.99, '/img/fruite-item-6.jpg', 60, 2),
('FRU0007', '檸檬', '新鮮檸檬', 59.99, '/img/fruite-item-7.jpg', 110, 3);
=======
('FRU0001', '蘋果', '新鮮紅蘋果', 99.99, 'https://example.com/apple.jpg', 100, 1),
('FRU0002', '香蕉', '新鮮黃香蕉', 49.99, 'https://example.com/banana.jpg', 150, 2),
('FRU0003', '橙子', '新鮮橙子', 79.99, 'https://example.com/orange.jpg', 120, 3),
('FRU0004', '葡萄', '新鮮紫葡萄', 149.99, 'https://example.com/grape.jpg', 80, 1),
('FRU0005', '鳳梨', '新鮮甜鳳梨', 119.99, 'https://example.com/pineapple.jpg', 90, 2),
('FRU0006', '西瓜', '新鮮西瓜', 199.99, 'https://example.com/watermelon.jpg', 60, 2),
('FRU0007', '檸檬', '新鮮檸檬', 59.99, 'https://example.com/lemon.jpg', 110, 3);
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac:db-scripts/eeit147-5thSQL-ver2.43.txt

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

<<<<<<< HEAD:db-scripts/eeit147-5thSQL-ver2.45.sql

=======
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac:db-scripts/eeit147-5thSQL-ver2.43.txt
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

-- 插入假資料到 reviews 表
INSERT INTO reviews (reviews_product_id, reviews_user_id, rating, comment, updated_at) 
VALUES 
(1, 1, 5, N'非常好用，效果出乎意料。很滿意這次的購物體驗，值得推薦。', DATEADD(MINUTE, -10, GETDATE())),
(2, 2, 4, N'商品質量還不錯，但物流稍慢了一點。', DATEADD(MINUTE, -10, GETDATE())),
(3, 3, 3, N'普通商品，沒有太大驚喜。性價比一般。', DATEADD(MINUTE, -10, GETDATE())),
(4, 4, 2, N'商品與描述不符，質量不好，不太滿意。', DATEADD(MINUTE, -10, GETDATE())),
(5, 5, 5, N'超級喜歡這款產品，物超所值，非常實用。', DATEADD(MINUTE, -10, GETDATE()));

<<<<<<< HEAD:db-scripts/eeit147-5thSQL-ver2.45.sql
INSERT INTO [dbo].[coupon] ([coupon_code], [target_amount], [discount_amount], [end_date]) VALUES (N'SAVE50', 300, 50, CAST(N'2025-06-30T23:59:59.000' AS DateTime))
INSERT INTO [dbo].[coupon] ([coupon_code], [target_amount], [discount_amount], [end_date]) VALUES (N'DISCOUNT100', 600, 100, CAST(N'2025-12-31T23:59:59.000' AS DateTime))
INSERT INTO [dbo].[coupon] ([coupon_code], [target_amount], [discount_amount], [end_date]) VALUES (N'NEWYEAR2025', 10000, 8000, CAST(N'2025-01-01T23:59:59.000' AS DateTime))
INSERT INTO [dbo].[coupon] ([coupon_code], [target_amount], [discount_amount], [end_date]) VALUES (N'SUMMER20', 1200, 400, CAST(N'2025-08-15T23:59:59.000' AS DateTime))
INSERT INTO [dbo].[coupon] ([coupon_code], [target_amount], [discount_amount], [end_date]) VALUES (N'FREESHIP', 2000, 500, CAST(N'2025-07-01T23:59:59.000' AS DateTime))

-- 新增邮箱验证表
CREATE TABLE email_verification(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    created_at DATETIME2(6),
    email NVARCHAR(100) NOT NULL,
    expires_at DATETIME2(6) NOT NULL,
    is_verified BIT,
    token NVARCHAR(255) NOT NULL
);

-- 新增密码重置令牌表
CREATE TABLE password_reset_tokens(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    expiry_date DATETIME2(6),
    token NVARCHAR(255),
    used BIT NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 新增角色表
CREATE TABLE roles(
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    description NVARCHAR(255),
    name NVARCHAR(255) NOT NULL UNIQUE
);

-- 新增角色权限表
CREATE TABLE role_permissions(
    role_id INT NOT NULL,
    permission NVARCHAR(255),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 新增用户角色关联表
CREATE TABLE user_roles(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 新增用户活动记录表
CREATE TABLE user_activity(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    activity_time DATETIME2(6) NOT NULL,
    activity_type NVARCHAR(50) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    device_info NVARCHAR(255),
    ip_address NVARCHAR(64),
    status NVARCHAR(20),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 新增用户操作日志表
CREATE TABLE user_operation_log(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    create_time DATETIME2(6) NOT NULL,
    description NVARCHAR(255),
    execution_time BIGINT,
    ip NVARCHAR(64) NOT NULL,
    method NVARCHAR(255) NOT NULL,
    operation NVARCHAR(100) NOT NULL,
    params NVARCHAR(MAX) NOT NULL,
    username NVARCHAR(50) NOT NULL
);

-- 创建索引
CREATE UNIQUE NONCLUSTERED INDEX [UKla2ts67g4oh2sreayswhox1i6] ON [dbo].[password_reset_tokens]([user_id]);
CREATE NONCLUSTERED INDEX [idx_activity_time] ON [dbo].[user_activity]([activity_time]);
CREATE NONCLUSTERED INDEX [idx_activity_type] ON [dbo].[user_activity]([activity_type]);
CREATE NONCLUSTERED INDEX [idx_user_id] ON [dbo].[user_activity]([user_id]);
CREATE NONCLUSTERED INDEX [idx_create_time] ON [dbo].[user_operation_log]([create_time]);
CREATE NONCLUSTERED INDEX [idx_username] ON [dbo].[user_operation_log]([username]);
=======
-- 插入假資料到 coupon 表
INSERT INTO coupon (user_id, users_id, coupon_discount, coupon_date_timeout) 
VALUES 
(1, 1, 60, '2025-03-31 23:59:59'),
(2, 2, 60, '2025-04-30 23:59:59');
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac:db-scripts/eeit147-5thSQL-ver2.43.txt
