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
	[coupon_code] [varchar](50) NOT NULL,
	[target_amount] [int] NOT NULL,
	[discount_amount] [int] NOT NULL,
	[end_date] [datetime] NOT NULL,
);


-- 插入商品分類資料
INSERT INTO product_category (category_name)
VALUES ('水果'), ('熱帶水果'), ('柑橘類水果');


-- 插入水果商品資料，圖片路徑指向本機專案的靜態資源
INSERT INTO products (sku, product_name, [description], unit_price, image_url, unit_in_stock, category_id)
VALUES 
('FRU0001', '蘋果', '新鮮紅蘋果', 50, '/img/fruite-item-1.jpg', 100, 1),
('FRU0002', '香蕉', '新鮮黃香蕉', 15, '/img/fruite-item-2.jpg', 150, 2),
('FRU0003', '橙子', '新鮮橙子', 25, '/img/fruite-item-3.jpg', 120, 3),
('FRU0004', '葡萄', '新鮮紫葡萄', 80, '/img/fruite-item-4.jpg', 80, 1),
('FRU0005', '鳳梨', '新鮮甜鳳梨', 150, '/img/fruite-item-5.jpg', 90, 2),
('FRU0006', '西瓜', '新鮮西瓜', 126, '/img/fruite-item-6.jpg', 60, 2),
('FRU0007', '檸檬', '新鮮檸檬', 18, '/img/fruite-item-7.jpg', 110, 3);

-- 插入假資料到 users 表
-- [password] 統一為'123'
INSERT INTO users (username, [password], email, full_name, phone, user_photo, [address]) VALUES
('alice123', '$2a$12$EWhc0y62ujS.a2bCHSikaupEvl/eVYPPKWyir49iFqF2zEzSO.XeG.', 'alice@example.com', 'Alice Johnson', '0912345678', 'alice.jpg', '台北市信義區123號'),
('bob456', '$2a$12$EWhc0y62ujS.a2bCHSikaupEvl/eVYPPKWyir49iFqF2zEzSO.XeG', 'bob@example.com', 'Bob Smith', '0923456789', 'bob.jpg', '台中市南屯區456號'),
('charlie789', '$2a$12$EWhc0y62ujS.a2bCHSikaupEvl/eVYPPKWyir49iFqF2zEzSO.XeG', 'charlie@example.com', 'Charlie Brown', '0934567890', 'charlie.jpg', '高雄市三民區789號'),
('tomlee', '$2a$12$EWhc0y62ujS.a2bCHSikaupEvl/eVYPPKWyir49iFqF2zEzSO.XeG', 'tomlee@example.com', 'Tom Lee', '0911222333', 'tomlee.jpg', '台北市中正區100號'),
('janewang', '$2a$12$EWhc0y62ujS.a2bCHSikaupEvl/eVYPPKWyir49iFqF2zEzSO.XeG', 'janewang@example.com', 'Jane Wang', '0922333444', 'janewang.jpg', '台南市東區200號');


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
