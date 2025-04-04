/****** Object:  Table [dbo].[Best_Sell_Rankings]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Best_Sell_Rankings](
	[best_sell_rankings_id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [int] NOT NULL,
	[number_count] [int] NOT NULL,
	[bsr_discount] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[best_sell_rankings_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Best_Sell_Tracking]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Best_Sell_Tracking](
	[best_sell_tracking_id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [int] NOT NULL,
	[love_count] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[best_sell_tracking_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[buy_one_get_one]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[buy_one_get_one](
	[bogo_id] [int] IDENTITY(1,1) NOT NULL,
	[order_id] [int] NOT NULL,
	[users_id] [int] NOT NULL,
	[product_id] [int] NOT NULL,
	[quantity] [int] NULL,
	[bogo_condition] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[bogo_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Coupon]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Coupon](
	[coupon_id] [int] IDENTITY(1,1) NOT NULL,
	[users_id] [int] NOT NULL,
	[coupon_discount] [int] NOT NULL,
	[coupon_date_timeout] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[coupon_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[discount]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[discount](
	[discount_id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [int] NOT NULL,
	[date_time] [datetime] NOT NULL,
	[discount_percent] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[discount_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Limited_time_sale]    Script Date: 2025/3/4 下午 09:58:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Limited_time_sale](
	[limited_time_sale_id] [int] IDENTITY(1,1) NOT NULL,
	[limited_time_start] [datetime] NOT NULL,
	[limited_time_end] [datetime] NOT NULL,
	[limited_time_list_id] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[limited_time_sale_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[Best_Sell_Rankings] ON 

INSERT [dbo].[Best_Sell_Rankings] ([best_sell_rankings_id], [product_id], [number_count], [bsr_discount]) VALUES (1, 23, 14, 250)
INSERT [dbo].[Best_Sell_Rankings] ([best_sell_rankings_id], [product_id], [number_count], [bsr_discount]) VALUES (2, 11, 90, 199)
SET IDENTITY_INSERT [dbo].[Best_Sell_Rankings] OFF
GO
SET IDENTITY_INSERT [dbo].[Best_Sell_Tracking] ON 

INSERT [dbo].[Best_Sell_Tracking] ([best_sell_tracking_id], [product_id], [love_count]) VALUES (1, 11, 1000)
SET IDENTITY_INSERT [dbo].[Best_Sell_Tracking] OFF
GO
SET IDENTITY_INSERT [dbo].[buy_one_get_one] ON 

INSERT [dbo].[buy_one_get_one] ([bogo_id], [order_id], [users_id], [product_id], [quantity], [bogo_condition]) VALUES (1, 21, 1, 11, 20, 0)
SET IDENTITY_INSERT [dbo].[buy_one_get_one] OFF
GO
SET IDENTITY_INSERT [dbo].[Coupon] ON 

INSERT [dbo].[Coupon] ([coupon_id], [users_id], [coupon_discount], [coupon_date_timeout]) VALUES (2, 101, 60, CAST(N'2025-03-31T23:59:59.000' AS DateTime))
INSERT [dbo].[Coupon] ([coupon_id], [users_id], [coupon_discount], [coupon_date_timeout]) VALUES (3, 102, 60, CAST(N'2025-04-30T23:59:59.000' AS DateTime))
SET IDENTITY_INSERT [dbo].[Coupon] OFF
GO
SET IDENTITY_INSERT [dbo].[discount] ON 

INSERT [dbo].[discount] ([discount_id], [product_id], [date_time], [discount_percent]) VALUES (1, 101, CAST(N'2025-03-01T10:00:00.000' AS DateTime), 75)
INSERT [dbo].[discount] ([discount_id], [product_id], [date_time], [discount_percent]) VALUES (2, 102, CAST(N'2025-04-01T08:30:00.000' AS DateTime), 80)
SET IDENTITY_INSERT [dbo].[discount] OFF
GO
SET IDENTITY_INSERT [dbo].[Limited_time_sale] ON 

INSERT [dbo].[Limited_time_sale] ([limited_time_sale_id], [limited_time_start], [limited_time_end], [limited_time_list_id]) VALUES (1, CAST(N'2025-03-01T10:00:00.000' AS DateTime), CAST(N'2025-04-01T08:30:00.000' AS DateTime), 1)
INSERT [dbo].[Limited_time_sale] ([limited_time_sale_id], [limited_time_start], [limited_time_end], [limited_time_list_id]) VALUES (2, CAST(N'2025-03-01T10:00:00.000' AS DateTime), CAST(N'2025-04-15T08:30:00.000' AS DateTime), 2)
SET IDENTITY_INSERT [dbo].[Limited_time_sale] OFF
GO
