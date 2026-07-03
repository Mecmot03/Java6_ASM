/*==========================================================
    DATABASE: THE GIOI DIEN TU
==========================================================*/

IF DB_ID('TheGioiDienTu') IS NOT NULL
BEGIN
    ALTER DATABASE TheGioiDienTu SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE TheGioiDienTu;
END
GO

CREATE DATABASE TheGioiDienTu;
GO

USE TheGioiDienTu;
GO

/*==========================================================
    USERS
==========================================================*/

CREATE TABLE Users
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    FullName NVARCHAR(100) NOT NULL,

    Email NVARCHAR(100) NOT NULL UNIQUE,

    Password NVARCHAR(255) NOT NULL,

    Phone NVARCHAR(20),

    Address NVARCHAR(300),

    Avatar NVARCHAR(255),

    Enabled BIT DEFAULT 1,

    CreatedAt DATETIME DEFAULT GETDATE(),

    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

/*==========================================================
    AUTHORITIES
==========================================================*/

CREATE TABLE Authorities
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    UserId BIGINT NOT NULL,

    Role NVARCHAR(30) NOT NULL,

    CONSTRAINT FK_Authorities_Users
        FOREIGN KEY(UserId)
        REFERENCES Users(Id)
        ON DELETE CASCADE
);
GO

/*==========================================================
    CATEGORIES
==========================================================*/

CREATE TABLE Categories
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    Name NVARCHAR(100) NOT NULL,

    Description NVARCHAR(500),

    Image NVARCHAR(255),

    Status BIT DEFAULT 1
);
GO

/*==========================================================
    DISCOUNTS
==========================================================*/

CREATE TABLE Discounts
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    Code NVARCHAR(50),

    DiscountPercent INT,

    StartDate DATE,

    EndDate DATE,

    Status BIT DEFAULT 1
);
GO

/*==========================================================
    PRODUCTS
==========================================================*/

CREATE TABLE Products
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    CategoryId BIGINT NOT NULL,

    DiscountId BIGINT NULL,

    Name NVARCHAR(255) NOT NULL,

    Description NVARCHAR(MAX),

    Price DECIMAL(18,2) NOT NULL,

    Quantity INT DEFAULT 0,

    Image NVARCHAR(255),

    Brand NVARCHAR(100),

    Status BIT DEFAULT 1,

    CreatedAt DATETIME DEFAULT GETDATE(),

    UpdatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Product_Category
        FOREIGN KEY(CategoryId)
        REFERENCES Categories(Id),

    CONSTRAINT FK_Product_Discount
        FOREIGN KEY(DiscountId)
        REFERENCES Discounts(Id)
);
GO

/*==========================================================
    CART ITEMS
==========================================================*/

CREATE TABLE Cart_Items
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    UserId BIGINT NOT NULL,

    ProductId BIGINT NOT NULL,

    Quantity INT DEFAULT 1,

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Cart_User
        FOREIGN KEY(UserId)
        REFERENCES Users(Id)
        ON DELETE CASCADE,

    CONSTRAINT FK_Cart_Product
        FOREIGN KEY(ProductId)
        REFERENCES Products(Id)
);
GO

/*==========================================================
    FAVORITES
==========================================================*/

CREATE TABLE Favorites
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    UserId BIGINT NOT NULL,

    ProductId BIGINT NOT NULL,

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Favorite_User
        FOREIGN KEY(UserId)
        REFERENCES Users(Id)
        ON DELETE CASCADE,

    CONSTRAINT FK_Favorite_Product
        FOREIGN KEY(ProductId)
        REFERENCES Products(Id)
);
GO

/*==========================================================
    COMMENTS
==========================================================*/

CREATE TABLE Comments
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    UserId BIGINT NOT NULL,

    ProductId BIGINT NOT NULL,

    Content NVARCHAR(MAX),

    Rating INT CHECK(Rating BETWEEN 1 AND 5),

    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Comment_User
        FOREIGN KEY(UserId)
        REFERENCES Users(Id)
        ON DELETE CASCADE,

    CONSTRAINT FK_Comment_Product
        FOREIGN KEY(ProductId)
        REFERENCES Products(Id)
        ON DELETE CASCADE
);
GO

/*==========================================================
    ORDERS
==========================================================*/

CREATE TABLE Orders
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    UserId BIGINT NOT NULL,

    OrderDate DATETIME DEFAULT GETDATE(),

    ReceiverName NVARCHAR(100),

    ReceiverPhone NVARCHAR(20),

    ShippingAddress NVARCHAR(500),

    TotalAmount DECIMAL(18,2),

    PaymentMethod NVARCHAR(50),

    Status NVARCHAR(50),

    CONSTRAINT FK_Order_User
        FOREIGN KEY(UserId)
        REFERENCES Users(Id)
);
GO

/*==========================================================
    ORDER ITEMS
==========================================================*/

CREATE TABLE Order_Items
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,

    OrderId BIGINT NOT NULL,

    ProductId BIGINT NOT NULL,

    Quantity INT NOT NULL,

    Price DECIMAL(18,2),

    CONSTRAINT FK_OrderItem_Order
        FOREIGN KEY(OrderId)
        REFERENCES Orders(Id)
        ON DELETE CASCADE,

    CONSTRAINT FK_OrderItem_Product
        FOREIGN KEY(ProductId)
        REFERENCES Products(Id)
);
GO

/*==========================================================
    INDEX
==========================================================*/

CREATE INDEX IDX_Product_Name
ON Products(Name);

CREATE INDEX IDX_Product_Category
ON Products(CategoryId);

CREATE INDEX IDX_Cart_User
ON Cart_Items(UserId);

CREATE INDEX IDX_Order_User
ON Orders(UserId);

CREATE INDEX IDX_Comment_Product
ON Comments(ProductId);

GO

/*==========================================================
    SAMPLE DATA
==========================================================*/

INSERT INTO Categories(Name,Description,Image)
VALUES
(N'Điện thoại',N'Điện thoại thông minh','phone.jpg'),
(N'Laptop',N'Laptop văn phòng và Gaming','laptop.jpg'),
(N'Máy tính bảng',N'Tablet','tablet.jpg'),
(N'Tai nghe',N'Tai nghe Bluetooth','earphone.jpg'),
(N'Đồng hồ thông minh',N'Smart Watch','watch.jpg');

INSERT INTO Discounts
(Code,DiscountPercent,StartDate,EndDate)
VALUES
('SALE10',10,'2026-01-01','2026-12-31'),
('SALE20',20,'2026-01-01','2026-12-31');

INSERT INTO Users
(FullName,Email,Password,Phone,Address)
VALUES
(N'Administrator',
'admin@gmail.com',
'123456',
'0900000000',
N'TP Hồ Chí Minh');

INSERT INTO Authorities(UserId,Role)
VALUES
(1,'ROLE_ADMIN');

INSERT INTO Products
(
CategoryId,
DiscountId,
Name,
Description,
Price,
Quantity,
Image,
Brand
)
VALUES
(
1,
1,
N'iPhone 16 Pro',
N'Điện thoại Apple',
32990000,
20,
'iphone16.jpg',
'Apple'
),
(
1,
2,
N'Samsung Galaxy S26',
N'Điện thoại Samsung',
26990000,
30,
's26.jpg',
'Samsung'
),
(
2,
NULL,
N'MacBook Air M5',
N'Laptop Apple',
32990000,
15,
'macbook.jpg',
'Apple'
),
(
2,
NULL,
N'Asus ROG Zephyrus',
N'Laptop Gaming',
39990000,
10,
'rog.jpg',
'Asus'
),
(
4,
NULL,
N'AirPods Pro 3',
N'Tai nghe Apple',
6990000,
50,
'airpods.jpg',
'Apple'
);

GO