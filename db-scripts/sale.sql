CREATE TABLE [dbo].[users] (
    [member_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [username] NVARCHAR(100) unique NOT NULL,
    [email] VARCHAR(100) unique NOT NULL,
    [password] VARCHAR(100) NOT NULL,
    [full_name] NVARCHAR(100) NOT NULL,
    [phone] VARCHAR(100) NOT NULL,
    [user_photo] varbinary(max),
    [address] nvarchar(100)
);

create table categories (
[category_id] INT NOT NULL PRIMARY KEY,
[category_name] NVARCHAR(100),
[parent_id] INT)
 
CREATE TABLE [dbo].[products] (
    [product_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [product_name] NVARCHAR(100) NOT NULL,
    [product_description] NVARCHAR(2000),
    [product_price] INT NOT NULL,
    [product_stock] INT NOT NULL,
    [product_category_id] INT NOT NULL,  -- 這是外鍵，指向 categories 表
    [p_shop_id] INT NOT NULL,  -- 這是外鍵，指向 shops 表
    [product_photo] VARBINARY(MAX),
    [product_status] INT,
    CONSTRAINT FK_Product_Category FOREIGN KEY ([product_category_id]) REFERENCES [dbo].[categories]([category_id]),
    CONSTRAINT FK_Product_Shop FOREIGN KEY ([p_shop_id]) REFERENCES [dbo].[shops]([shop_id])
);

INSERT INTO [dbo].[users] 
([username], [email], [password], [full_name], [phone], [user_photo], [address])
VALUES
('john_doe', 'john.doe@example.com', 'hashedpassword123', 'John Doe', '123-456-7890', NULL, '80147高雄市前金區中正四路211號8樓之1'),
('jane_smith', 'jane.smith@example.com', 'hashedpassword456', 'Jane Smith', '234-567-8901', NULL, '80147高雄市前金區中正四路211號8樓之1'),
('alex_jones', 'alex.jones@example.com', 'hashedpassword789', 'Alex Jones', '345-678-9012', NULL, '80147高雄市前金區中正四路211號8樓之1'),
('lisa_white', 'lisa.white@example.com', 'hashedpassword321', 'Lisa White', '456-789-0123', NULL, '80147高雄市前金區中正四路211號8樓之1'),
('mark_brown', 'mark.brown@example.com', 'hashedpassword654', 'Mark Brown', '567-890-1234', NULL, '80147高雄市前金區中正四路211號8樓之1');

insert into categories
 ([category_id],[category_name],[parent_id])
 values
 (1,'電子產品',null),
 (11,'穿戴式裝置',1),
 (12,'電視',1),
 (13,'行動電源',1),
 (14,'筆記型電腦',1)


INSERT INTO [dbo].[products] 
([product_name], [product_description], [product_price], [product_stock], [product_category_id], [p_shop_id], [product_photo], [product_status])
VALUES
('Wireless Bluetooth Headphones', 'High-quality wireless Bluetooth headphones with noise cancellation, 20-hour battery life, and comfortable over-ear design.', 120, 50, 11, 1, NULL, 1),
('4K Ultra HD Smart TV', '65-inch 4K Ultra HD Smart TV with HDR, Wi-Fi connectivity, and voice control. Perfect for streaming and gaming.', 800, 30, 12, 2, NULL, 1),
('Portable Power Bank', '10000mAh portable power bank with quick charge, lightweight design, and multiple USB ports for fast charging on the go.', 30, 100, 13, 3, NULL, 1),
('Gaming Laptop', 'High-performance gaming laptop with Intel i7 processor, 16GB RAM, and NVIDIA RTX 3060 graphics card. Ideal for gamers and content creators.', 1500, 20, 14, 4, NULL, 1),
('Smart Fitness Tracker', 'Waterproof smart fitness tracker with heart rate monitor, step counter, and sleep tracking. Syncs with your smartphone for health insights.', 50, 200, 11, 5, NULL, 1);

CREATE TABLE [dbo].[shops] (
    [shop_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [user_id] INT NOT NULL　FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([member_id]),
    [store_name] NVARCHAR(1000) NOT NULL,
    [store_description] NVARCHAR(1000),
    [created_at] DATETIME NOT NULL DEFAULT GETDATE(),
    [seller_photo] VARBINARY(MAX),
    [seller_status] TINYINT NOT NULL,
    [shop_status] BIT NOT NULL
);
INSERT INTO [dbo].[shops] 
([user_id], [store_name], [store_description], [created_at], [seller_status], [shop_status])
VALUES
(1, '美味小吃店', '提供各式小吃，口味獨特，絕對讓你回味無窮。', GETDATE(), 1, 1),
(2, 'Fashion World', '最流行的時尚服飾店，讓你成為街頭最亮眼的存在。', GETDATE(), 1, 1),
(3, '舒適家居館', '舒適的居家生活用品，讓你在家也能享受度假感覺。', GETDATE(), 1, 1),
(4, '綠意花園', '提供新鮮的植物和花卉，裝點你的家，讓生活更有生氣。', GETDATE(), 1, 1),
(5, 'Tech Gadget Store', '最新科技產品，讓你領先潮流。', GETDATE(), 1, 1),
(1, '手工藝品專賣店', '每一個手工藝品都充滿藝術氣息，為您的家增添獨特風格。', GETDATE(), 1, 1),
(2, '運動用品專賣店', '提供各式運動用品，讓你輕鬆開啟運動生活。', GETDATE(), 1, 1),
(3, '美妝與保養', '專業的美容與保養產品，讓你的肌膚永遠年輕光滑。', GETDATE(), 1, 1),
(4, '電玩世界', '遊戲愛好者的天堂，讓你享受無限的遊戲樂趣。', GETDATE(), 1, 1),
(5, '寵物用品店', '為你的寵物挑選最合適的用品，讓牠們的生活更幸福。', GETDATE(), 1, 1);

CREATE TABLE [dbo].[reviews] (
    [review_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- 主鍵，自動遞增
    [product_id] INT NOT NULL,  -- 外鍵，參考 products 表格的 product_id
    [user_id] INT NOT NULL,  -- 外鍵，參考 users 表格的 member_id
    [rating] INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,  -- 評分，介於 1 到 5 之間
    [comment] NVARCHAR(1000),  -- 評論內容
    [photo] VARBINARY(MAX),  -- 圖片，儲存為二進制數據
    [created_at] DATETIME NOT NULL DEFAULT GETDATE(),  -- 創建時間，預設為當前時間
    [updated_at] DATETIME NOT NULL DEFAULT GETDATE(),  -- 更新時間，預設為當前時間
    CONSTRAINT FK_Product FOREIGN KEY ([product_id]) REFERENCES [dbo].[products]([product_id]),  -- 外鍵約束：product_id 參考 products 表格
    CONSTRAINT FK_User FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([member_id])  -- 外鍵約束：user_id 參考 users 表格
);
INSERT INTO [dbo].[reviews] ([product_id], [user_id], [rating], [comment], [created_at], [updated_at]) 
VALUES 
    (1, 1, 5, N'非常好用，效果出乎意料。很滿意這次的購物體驗，值得推薦。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (2, 2, 4, N'商品質量還不錯，但物流稍慢了一點。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (3, 3, 3, N'普通商品，沒有太大驚喜。性價比一般。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (4, 4, 2, N'商品與描述不符，質量不好，不太滿意。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (5, 5, 5, N'超級喜歡這款產品，物超所值，非常實用。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (1, 2, 4, N'質量很好，使用起來很方便，適合日常使用。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (2, 3, 3, N'商品還可以，沒有很特別，適合基本需求。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (3, 4, 1, N'商品質量差，完全不符合預期。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (4, 5, 4, N'商品不錯，符合描述，但還是希望能再提升質量。', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (5, 1, 5, N'這款產品完全符合我的需求，性價比超高，會再次購買。', DATEADD(MINUTE, -10, GETDATE()), GETDATE());
