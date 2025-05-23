-- 客服對話紀錄表
CREATE TABLE ChatSessions (
    ChatID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID) ON DELETE CASCADE,
    SupportID INT NULL FOREIGN KEY REFERENCES Users(UserID) ON DELETE SET NULL,
    Status NVARCHAR(20) CHECK (Status IN ('Open', 'Closed')) DEFAULT 'Open',
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    ClosedAt DATETIME NULL
);
GO

-- 訊息表
CREATE TABLE Messages (
    MessageID INT IDENTITY(1,1) PRIMARY KEY,
    ChatID INT NOT NULL FOREIGN KEY REFERENCES ChatSessions(ChatID) ON DELETE CASCADE,
    SenderID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID) ON DELETE CASCADE,
    MessageText NVARCHAR(MAX) NOT NULL,
    IsAIResponse BIT DEFAULT 0, -- 0: 人類, 1: AI 回覆
    SentAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 常見問題表
CREATE TABLE FAQs (
    FAQID INT IDENTITY(1,1) PRIMARY KEY,
    Question NVARCHAR(500) NOT NULL,
    Answer NVARCHAR(1000) NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 客戶回饋表
CREATE TABLE Feedback (
    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL FOREIGN KEY REFERENCES Users(UserID) ON DELETE CASCADE,
    Rating INT CHECK (Rating BETWEEN 1 AND 5) NOT NULL,
    Comment NVARCHAR(1000),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO