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
    [product_category_id] INT NOT NULL,  -- �o�O�~��A���V categories ��
    [p_shop_id] INT NOT NULL,  -- �o�O�~��A���V shops ��
    [product_photo] VARBINARY(MAX),
    [product_status] INT,
    CONSTRAINT FK_Product_Category FOREIGN KEY ([product_category_id]) REFERENCES [dbo].[categories]([category_id]),
    CONSTRAINT FK_Product_Shop FOREIGN KEY ([p_shop_id]) REFERENCES [dbo].[shops]([shop_id])
);

INSERT INTO [dbo].[users] 
([username], [email], [password], [full_name], [phone], [user_photo], [address])
VALUES
('john_doe', 'john.doe@example.com', 'hashedpassword123', 'John Doe', '123-456-7890', NULL, '80147�������e���Ϥ����|��211��8�Ӥ�1'),
('jane_smith', 'jane.smith@example.com', 'hashedpassword456', 'Jane Smith', '234-567-8901', NULL, '80147�������e���Ϥ����|��211��8�Ӥ�1'),
('alex_jones', 'alex.jones@example.com', 'hashedpassword789', 'Alex Jones', '345-678-9012', NULL, '80147�������e���Ϥ����|��211��8�Ӥ�1'),
('lisa_white', 'lisa.white@example.com', 'hashedpassword321', 'Lisa White', '456-789-0123', NULL, '80147�������e���Ϥ����|��211��8�Ӥ�1'),
('mark_brown', 'mark.brown@example.com', 'hashedpassword654', 'Mark Brown', '567-890-1234', NULL, '80147�������e���Ϥ����|��211��8�Ӥ�1');

insert into categories
 ([category_id],[category_name],[parent_id])
 values
 (1,'�q�l���~',null),
 (11,'�������˸m',1),
 (12,'�q��',1),
 (13,'��ʹq��',1),
 (14,'���O���q��',1)


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
    [user_id] INT NOT NULL�@FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([member_id]),
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
(1, '�����p�Y��', '���ѦU���p�Y�A�f���W�S�A�������A�^���L�a�C', GETDATE(), 1, 1),
(2, 'Fashion World', '�̬y�檺�ɩ|�A�����A���A�������Y�̫G�����s�b�C', GETDATE(), 1, 1),
(3, '�ξA�a�~�]', '�ξA���~�a�ͬ��Ϋ~�A���A�b�a�]��ɨ��װ��Pı�C', GETDATE(), 1, 1),
(4, '��N���', '���ѷs�A���Ӫ��M��c�A���I�A���a�A���ͬ��󦳥ͮ�C', GETDATE(), 1, 1),
(5, 'Tech Gadget Store', '�̷s��޲��~�A���A�����y�C', GETDATE(), 1, 1),
(1, '��u���~�M�橱', '�C�@�Ӥ�u���~���R�����N�𮧡A���z���a�W�K�W�S����C', GETDATE(), 1, 1),
(2, '�B�ʥΫ~�M�橱', '���ѦU���B�ʥΫ~�A���A���P�}�ҹB�ʥͬ��C', GETDATE(), 1, 1),
(3, '�����P�O�i', '�M�~�����e�P�O�i���~�A���A���ٽ��û��~�����ơC', GETDATE(), 1, 1),
(4, '�q���@��', '�C���R�n�̪��Ѱ�A���A�ɨ��L�����C���ֽ�C', GETDATE(), 1, 1),
(5, '�d���Ϋ~��', '���A���d���D��̦X�A���Ϋ~�A���e�̪��ͬ��󩯺֡C', GETDATE(), 1, 1);

CREATE TABLE [dbo].[reviews] (
    [review_id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- �D��A�۰ʻ��W
    [product_id] INT NOT NULL,  -- �~��A�Ѧ� products ���檺 product_id
    [user_id] INT NOT NULL,  -- �~��A�Ѧ� users ���檺 member_id
    [rating] INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,  -- �����A���� 1 �� 5 ����
    [comment] NVARCHAR(1000),  -- ���פ��e
    [photo] VARBINARY(MAX),  -- �Ϥ��A�x�s���G�i��ƾ�
    [created_at] DATETIME NOT NULL DEFAULT GETDATE(),  -- �Ыخɶ��A�w�]�����e�ɶ�
    [updated_at] DATETIME NOT NULL DEFAULT GETDATE(),  -- ��s�ɶ��A�w�]�����e�ɶ�
    CONSTRAINT FK_Product FOREIGN KEY ([product_id]) REFERENCES [dbo].[products]([product_id]),  -- �~������Gproduct_id �Ѧ� products ����
    CONSTRAINT FK_User FOREIGN KEY ([user_id]) REFERENCES [dbo].[users]([member_id])  -- �~������Guser_id �Ѧ� users ����
);
INSERT INTO [dbo].[reviews] ([product_id], [user_id], [rating], [comment], [created_at], [updated_at]) 
VALUES 
    (1, 1, 5, N'�D�`�n�ΡA�ĪG�X�G�N�ơC�ܺ��N�o�����ʪ�����A�ȱo���ˡC', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (2, 2, 4, N'�ӫ~��q�٤����A�����y�y�C�F�@�I�C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (3, 3, 3, N'���q�ӫ~�A�S���Ӥj��ߡC�ʻ���@��C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (4, 4, 2, N'�ӫ~�P�y�z���šA��q���n�A���Ӻ��N�C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (5, 5, 5, N'�W�ų��w�o�ڲ��~�A���W�ҭȡA�D�`��ΡC', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (1, 2, 4, N'��q�ܦn�A�ϥΰ_�ӫܤ�K�A�A�X��`�ϥΡC', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (2, 3, 3, N'�ӫ~�٥i�H�A�S���ܯS�O�A�A�X�򥻻ݨD�C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (3, 4, 1, N'�ӫ~��q�t�A�������ŦX�w���C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (4, 5, 4, N'�ӫ~�����A�ŦX�y�z�A���٬O�Ʊ��A���ɽ�q�C', DATEADD(MINUTE, -10, GETDATE()), GETDATE()),
    (5, 1, 5, N'�o�ڲ��~�����ŦX�ڪ��ݨD�A�ʻ���W���A�|�A���ʶR�C', DATEADD(MINUTE, -10, GETDATE()), GETDATE());
