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


-- 客服對話紀錄表
CREATE TABLE chat_sessions (
    chat_id INT IDENTITY(1,1) PRIMARY KEY,
    chat_user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
    support_id INT NULL FOREIGN KEY REFERENCES users(user_id),
    Status NVARCHAR(20) CHECK (Status IN ('Open', 'Closed')) DEFAULT 'Open',
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    closed_at DATETIME NULL
);
GO

-- 訊息表
CREATE TABLE messages (
    message_id INT IDENTITY(1,1) PRIMARY KEY,
    chat_id INT NOT NULL FOREIGN KEY REFERENCES chat_sessions(chat_id),
    sender_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
    message_text NVARCHAR(MAX) NOT NULL,
    is_ai_response BIT DEFAULT 0, -- 0: 人類, 1: AI 回覆
    sent_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- 常見問題表
CREATE TABLE faqs (
    faq_id INT IDENTITY(1,1) PRIMARY KEY,
    question NVARCHAR(500) NOT NULL,
    answer NVARCHAR(1000) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- 客戶回饋表
CREATE TABLE feedback (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    feedback_user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id) ON DELETE CASCADE,
    rating INT CHECK (Rating BETWEEN 1 AND 5) NOT NULL,
    comment NVARCHAR(1000),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE [dbo].[user_vip](
	[end_date] [date] NULL,
	[is_vip] [bit] NOT NULL,
	[user_id] [int] NULL,
	[start_date] [date] NOT NULL,
	[vip_id] [int] IDENTITY(1,1) NOT NULL,
	[vip_level] [int] NULL,
	[vip_photo] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[vip_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[user_vip_history](
	[end_date] [date] NOT NULL,
	[history_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NULL,
	[start_date] [date] NOT NULL,
	[vip_level] [int] NULL,
	[vip_photo] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[history_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO


CREATE TABLE [dbo].[reviews] (
    [review_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- 主鍵，自動遞增
    [reviews_product_id] INT NOT NULL,  -- 外鍵，參考 products 表格的 product_id
    [reviews_user_id] INT NOT NULL,  -- 外鍵，參考 users 表格的 user_id
    [rating] INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,  -- 評分，介於 1 到 5 之間
    [comment] NVARCHAR(1000),  -- 評論內容
    [photo] VARBINARY(MAX),  -- 圖片，儲存為二進制數據
    [updated_at] DATETIME NOT NULL DEFAULT GETDATE(),  -- 更新時間，預設為當前時間
    CONSTRAINT FK_Product FOREIGN KEY ([reviews_product_id]) REFERENCES [dbo].[products]([product_id]),  -- 外鍵約束：product_id 參考 products 表格
    CONSTRAINT FK_User FOREIGN KEY ([reviews_user_id]) REFERENCES [dbo].[users]([user_id])  -- 外鍵約束：user_id 參考 users 表格
);

CREATE TABLE [dbo].[coupon](
	[coupon_id] [int] IDENTITY(1,1) NOT NULL,
	[users_id] [int] NOT NULL,
	[coupon_discount] [int] NOT NULL,
	[coupon_date_timeout] [datetime] NOT NULL
);


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
('FRU0001', '蘋果', '新鮮紅蘋果', 99.99, '/img/fruite-item-1.jpg', 100, 1),
('FRU0002', '香蕉', '新鮮黃香蕉', 49.99, '/img/fruite-item-2.jpg', 150, 2),
('FRU0003', '橙子', '新鮮橙子', 79.99, '/img/fruite-item-3.jpg', 120, 3),
('FRU0004', '葡萄', '新鮮紫葡萄', 149.99, '/img/fruite-item-4.jpg', 80, 1),
('FRU0005', '鳳梨', '新鮮甜鳳梨', 119.99, '/img/fruite-item-5.jpg', 90, 2),
('FRU0006', '西瓜', '新鮮西瓜', 199.99, '/img/fruite-item-6.jpg', 60, 2),
('FRU0007', '檸檬', '新鮮檸檬', 59.99, '/img/fruite-item-7.jpg', 110, 3);

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


INSERT INTO [dbo].[reviews] ([reviews_product_id], [reviews_user_id], [rating], [comment], [updated_at]) 
VALUES 
    (1, 1, 5, N'非常好用，效果出乎意料。很滿意這次的購物體驗，值得推薦。', DATEADD(MINUTE, -10, GETDATE())),
    (2, 2, 4, N'商品質量還不錯，但物流稍慢了一點。', DATEADD(MINUTE, -10, GETDATE())),
    (3, 3, 3, N'普通商品，沒有太大驚喜。性價比一般。', DATEADD(MINUTE, -10, GETDATE())),
    (4, 4, 2, N'商品與描述不符，質量不好，不太滿意。', DATEADD(MINUTE, -10, GETDATE())),
    (5, 5, 5, N'超級喜歡這款產品，物超所值，非常實用。', DATEADD(MINUTE, -10, GETDATE())),
    (1, 2, 4, N'質量很好，使用起來很方便，適合日常使用。', DATEADD(MINUTE, -10, GETDATE())),
    (2, 3, 3, N'商品還可以，沒有很特別，適合基本需求。', DATEADD(MINUTE, -10, GETDATE())),
    (3, 4, 1, N'商品質量差，完全不符合預期。', DATEADD(MINUTE, -10, GETDATE())),
    (4, 5, 4, N'商品不錯，符合描述，但還是希望能再提升質量。', DATEADD(MINUTE, -10, GETDATE())),
    (5, 1, 5, N'這款產品完全符合我的需求，性價比超高，會再次購買。', DATEADD(MINUTE, -10, GETDATE()));

INSERT [dbo].[coupon] ([users_id], [coupon_discount], [coupon_date_timeout]) VALUES (101, 60, CAST(N'2025-03-31T23:59:59.000' AS DateTime))
INSERT [dbo].[coupon] ([users_id], [coupon_discount], [coupon_date_timeout]) VALUES (102, 60, CAST(N'2025-04-30T23:59:59.000' AS DateTime))
