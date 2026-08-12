/*==========================================================
    DATABASE: THE GIOI DIEN TU (CHUẨN HOÁ ROLES & AUTHORITIES)
==========================================================*/

USE [master]
GO

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
    1. BẢNG USERS
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
    2. BẢNG ROLES & AUTHORITIES (DANH MỤC QUYỀN CHUẨN)
==========================================================*/
CREATE TABLE Roles (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    Name VARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);
GO

CREATE TABLE Authorities
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    RoleId BIGINT NOT NULL,
    CONSTRAINT FK_Authorities_Users FOREIGN KEY(UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    CONSTRAINT FK_Authorities_Roles FOREIGN KEY(RoleId) REFERENCES Roles(Id) ON DELETE CASCADE
);
GO

/*==========================================================
    3. BẢNG CATEGORIES & DISCOUNTS
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
    4. BẢNG PRODUCTS
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
    CONSTRAINT FK_Product_Category FOREIGN KEY(CategoryId) REFERENCES Categories(Id),
    CONSTRAINT FK_Product_Discount FOREIGN KEY(DiscountId) REFERENCES Discounts(Id)
);
GO

/*==========================================================
    5. BẢNG CART, FAVORITES & COMMENTS
==========================================================*/
CREATE TABLE Cart_Items
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    Quantity INT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Cart_User FOREIGN KEY(UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    CONSTRAINT FK_Cart_Product FOREIGN KEY(ProductId) REFERENCES Products(Id)
);
GO

CREATE TABLE Favorites
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Favorite_User FOREIGN KEY(UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    CONSTRAINT FK_Favorite_Product FOREIGN KEY(ProductId) REFERENCES Products(Id)
);
GO

CREATE TABLE Comments
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    Content NVARCHAR(MAX),
    Rating INT CHECK(Rating BETWEEN 1 AND 5),
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Comment_User FOREIGN KEY(UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    CONSTRAINT FK_Comment_Product FOREIGN KEY(ProductId) REFERENCES Products(Id) ON DELETE CASCADE
);
GO

/*==========================================================
    6. BẢNG ORDERS & ORDER ITEMS
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
    CONSTRAINT FK_Order_User FOREIGN KEY(UserId) REFERENCES Users(Id)
);
GO

CREATE TABLE Order_Items
(
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(18,2),
    CONSTRAINT FK_OrderItem_Order FOREIGN KEY(OrderId) REFERENCES Orders(Id) ON DELETE CASCADE,
    CONSTRAINT FK_OrderItem_Product FOREIGN KEY(ProductId) REFERENCES Products(Id)
);
GO

/*==========================================================
    INDEXES
==========================================================*/
CREATE INDEX IDX_Product_Name ON Products(Name);
CREATE INDEX IDX_Product_Category ON Products(CategoryId);
CREATE INDEX IDX_Cart_User ON Cart_Items(UserId);
CREATE INDEX IDX_Order_User ON Orders(UserId);
CREATE INDEX IDX_Comment_Product ON Comments(ProductId);
GO

/*==========================================================
    DỮ LIỆU MẪU: ROLES, USERS & AUTHORITIES
==========================================================*/

-- 1. Khởi tạo danh mục Role
INSERT INTO Roles (Name, Description)
VALUES
('ROLE_ADMIN', N'Quản trị viên'),
('ROLE_USER', N'Khách hàng'),
('ROLE_STAFF', N'Nhân viên bán hàng');

-- 2. Thêm Users mẫu (Mật khẩu '123456' dạng BCrypt Hash)
INSERT INTO Users (FullName, Email, Password, Phone, Address)
VALUES 
(N'Administrator', 'admin@gmail.com', '$2a$10$YT67zFYNOqBQ2g2aYhSv9.JTulDYpQWfNyZVMr51GpHsxvMUizNF.', '0900000000', N'TP Hồ Chí Minh'),
(N'Admin Tiềm Năng', 'admin2@gmail.com', '$2a$10$UxgWS63eJdPTUVKQMAT5geZqCPzCUOjO.2dRfqJvlN5mFDAa5sI3e', '0911111111', N'Hà Nội');

-- 3. Phân quyền cho Admin và Admin2
INSERT INTO Authorities (UserId, RoleId)
VALUES 
((SELECT Id FROM Users WHERE Email = 'admin@gmail.com'), (SELECT Id FROM Roles WHERE Name = 'ROLE_ADMIN')),
((SELECT Id FROM Users WHERE Email = 'admin2@gmail.com'), (SELECT Id FROM Roles WHERE Name = 'ROLE_ADMIN'));
GO

/*==========================================================
    DỮ LIỆU MẪU: CATEGORIES, DISCOUNTS & PRODUCTS
==========================================================*/

INSERT INTO Categories(Name, Description, Image)
VALUES
(N'Điện thoại', N'Điện thoại thông minh', 'phone.jpg'),
(N'Laptop', N'Laptop văn phòng và Gaming', 'laptop.jpg'),
(N'Máy tính bảng', N'Tablet', 'tablet.jpg'),
(N'Tai nghe', N'Tai nghe Bluetooth', 'earphone.jpg'),
(N'Đồng hồ thông minh', N'Smart Watch', 'watch.jpg'),
(N'Bàn phím', N'Bàn phím cơ và văn phòng', 'keyboard.jpg'),
(N'Chuột', N'Chuột Gaming và Văn phòng', 'mouse.jpg'),
(N'Cáp sạc', N'Cáp sạc điện thoại, laptop', 'cable.jpg');

INSERT INTO Discounts (Code, DiscountPercent, StartDate, EndDate)
VALUES
('SALE10', 10, '2026-01-01', '2026-12-31'),
('SALE20', 20, '2026-01-01', '2026-12-31');

INSERT INTO Products (CategoryId, DiscountId, Name, Description, Price, Quantity, Image, Brand)
VALUES
(1, 1, N'iPhone 16 Pro', N'Điện thoại Apple', 32990000, 20, 'iphone16.jpg', 'Apple'),
(1, 2, N'Samsung Galaxy S26', N'Điện thoại Samsung', 26990000, 30, 's26.jpg', 'Samsung'),
(2, NULL, N'MacBook Air M5', N'Laptop Apple', 32990000, 15, 'macbook.jpg', 'Apple'),
(2, NULL, N'Asus ROG Zephyrus', N'Laptop Gaming', 39990000, 10, 'rog.jpg', 'Asus'),
(4, NULL, N'AirPods Pro 3', N'Tai nghe Apple', 6990000, 50, 'airpods.jpg', 'Apple'),
(1, NULL, N'iPhone 16', N'Điện thoại Apple', 24990000, 30, 'iphone16_black.jpg', 'Apple'),
(1, NULL, N'iPhone 16 Plus', N'Điện thoại Apple', 27990000, 20, 'iphone16_plus.jpg', 'Apple'),
(1, NULL, N'Samsung Galaxy A57', N'Điện thoại Samsung', 9990000, 40, 'a57.jpg', 'Samsung'),
(1, NULL, N'Xiaomi 16 Pro', N'Điện thoại Xiaomi', 15990000, 25, 'xiaomi16.jpg', 'Xiaomi'),
(1, NULL, N'OPPO Reno16', N'Điện thoại OPPO', 11990000, 35, 'reno16.jpg', 'OPPO'),
(2, NULL, N'Dell XPS 15', N'Laptop Dell cao cấp', 45990000, 12, 'xps15.jpg', 'Dell'),
(2, NULL, N'HP Pavilion 15', N'Laptop HP', 18990000, 20, 'hppavilion15.jpg', 'HP'),
(2, NULL, N'Lenovo Legion 5', N'Laptop Gaming', 32990000, 15, 'legion5.jpg', 'Lenovo'),
(2, NULL, N'MSI Katana 17', N'Laptop Gaming', 29990000, 18, 'katana17.jpg', 'MSI'),
(2, NULL, N'Acer Nitro V', N'Laptop Gaming', 21990000, 25, 'nitrov.jpg', 'Acer'),
(2, NULL, N'Lenovo ThinkPad X1 Carbon Gen14', N'Laptop doanh nhân', 46990000, 12, 'x1carbon14.jpg', 'Lenovo'),
(2, NULL, N'Asus Zenbook 14 OLED', N'Laptop mỏng nhẹ', 28990000, 18, 'zenbook14.jpg', 'Asus'),
(2, NULL, N'HP Omen 16', N'Laptop Gaming', 37990000, 15, 'omen16.jpg', 'HP'),
(3, NULL, N'iPad Air M3', N'iPad Apple', 18990000, 20, 'ipadairm3.jpg', 'Apple'),
(3, NULL, N'iPad Pro M4', N'iPad Pro', 31990000, 15, 'ipadprom4.jpg', 'Apple'),
(3, NULL, N'Samsung Tab S11', N'Tablet Samsung', 19990000, 18, 'tabs11.jpg', 'Samsung'),
(3, NULL, N'Xiaomi Pad 8', N'Tablet Xiaomi', 11990000, 25, 'pad8.jpg', 'Xiaomi'),
(3, NULL, N'Lenovo Tab P12', N'Tablet Lenovo', 8990000, 20, 'tabp12.jpg', 'Lenovo'),
(3, NULL, N'iPad Mini 8', N'Máy tính bảng Apple', 16990000, 18, 'ipadmini8.jpg', 'Apple'),
(3, NULL, N'Huawei MatePad Pro 13.2', N'Máy tính bảng Huawei', 17990000, 16, 'matepad13.jpg', 'Huawei'),
(3, NULL, N'Honor Pad X10', N'Máy tính bảng Honor', 7990000, 25, 'honorpadx10.jpg', 'Honor'),
(3, NULL, N'Redmi Pad Pro', N'Máy tính bảng Xiaomi', 8990000, 30, 'redmipadpro.jpg', 'Xiaomi'),
(3, NULL, N'OPPO Pad 4 Pro', N'Máy tính bảng OPPO', 13990000, 20, 'oppopad4.jpg', 'OPPO'),
(4, NULL, N'Sony WH-1000XM6', N'Tai nghe Sony', 9990000, 20, 'sonyxm6.jpg', 'Sony'),
(4, NULL, N'Galaxy Buds 4 Pro', N'Tai nghe Samsung', 4990000, 30, 'buds4pro.jpg', 'Samsung'),
(4, NULL, N'JBL Tune 770NC', N'Tai nghe JBL', 2990000, 40, 'jbl770.jpg', 'JBL'),
(4, NULL, N'Logitech G Pro X', N'Tai nghe Gaming', 3490000, 25, 'gprox.jpg', 'Logitech'),
(4, NULL, N'Razer BlackShark V2', N'Tai nghe Gaming', 2790000, 20, 'blacksharkv2.jpg', 'Razer'),
(4, NULL, N'Bose QuietComfort Ultra', N'Tai nghe chống ồn', 10990000, 15, 'boseultra.jpg', 'Bose'),
(4, NULL, N'SteelSeries Arctis Nova 7', N'Tai nghe Gaming', 4990000, 20, 'nova7.jpg', 'SteelSeries'),
(4, NULL, N'HyperX Cloud III', N'Tai nghe Gaming', 2590000, 30, 'cloud3.jpg', 'HyperX'),
(4, NULL, N'Edifier W820NB Plus', N'Tai nghe Bluetooth', 1790000, 35, 'w820nb.jpg', 'Edifier'),
(5, NULL, N'Apple Watch Series 11', N'Apple Watch', 12990000, 20, 'watch11.jpg', 'Apple'),
(5, NULL, N'Galaxy Watch 8', N'Samsung Watch', 9990000, 25, 'watch8.jpg', 'Samsung'),
(5, NULL, N'Garmin Forerunner 975', N'Garmin Watch', 14990000, 10, 'garmin975.jpg', 'Garmin'),
(5, NULL, N'Huawei Watch GT6', N'Huawei Watch', 6990000, 25, 'gt6.jpg', 'Huawei'),
(5, NULL, N'Amazfit Balance 2', N'Amazfit Watch', 4990000, 35, 'balance2.jpg', 'Amazfit'),
(5, NULL, N'Apple Watch Ultra 3', N'Apple Watch', 23990000, 12, 'ultra3.jpg', 'Apple'),
(5, NULL, N'Garmin Fenix 8', N'Garmin Watch', 21990000, 10, 'fenix8.jpg', 'Garmin'),
(5, NULL, N'Coros Pace 4', N'Đồng hồ thể thao', 9990000, 20, 'pace4.jpg', 'Coros'),
(5, NULL, N'Xiaomi Watch S5', N'Smart Watch Xiaomi', 4990000, 25, 'watchs5.jpg', 'Xiaomi'),
(5, NULL, N'CMF Watch Pro 3', N'Đồng hồ thông minh', 2690000, 30, 'cmfwatch3.jpg', 'CMF'),
(6, NULL, N'Keychron K8 Pro', N'Bàn phím cơ', 2790000, 25, 'k8pro.jpg', 'Keychron'),
(6, NULL, N'Akko 5075B Plus', N'Bàn phím cơ', 2390000, 30, '5075b.jpg', 'Akko'),
(6, NULL, N'Logitech MX Keys S', N'Bàn phím văn phòng', 2990000, 20, 'mxkeys.jpg', 'Logitech'),
(6, NULL, N'Razer BlackWidow V4', N'Gaming Keyboard', 4990000, 15, 'blackwidowv4.jpg', 'Razer'),
(6, NULL, N'Royal Kludge RK84', N'Bàn phím cơ', 1490000, 30, 'rk84.jpg', 'RK'),
(6, NULL, N'Keychron Q1 Max', N'Bàn phím cơ', 5290000, 15, 'q1max.jpg', 'Keychron'),
(6, NULL, N'Akko MOD007 HE', N'Bàn phím Hall Effect', 4290000, 18, 'mod007he.jpg', 'Akko'),
(6, NULL, N'NuPhy Air75 V3', N'Bàn phím Low Profile', 3690000, 20, 'air75v3.jpg', 'NuPhy'),
(6, NULL, N'Corsair K70 Max', N'Gaming Keyboard', 4990000, 15, 'k70max.jpg', 'Corsair'),
(6, NULL, N'IQUNIX ZX75', N'Bàn phím cơ', 3890000, 18, 'zx75.jpg', 'IQUNIX'),
(7, NULL, N'Logitech G Pro X Superlight 2', N'Gaming Mouse', 3690000, 20, 'gprox2.jpg', 'Logitech'),
(7, NULL, N'Razer Viper V3 Pro', N'Gaming Mouse', 4190000, 15, 'viperv3.jpg', 'Razer'),
(7, NULL, N'Logitech MX Master 3S', N'Chuột văn phòng', 2490000, 30, 'mxmaster3s.jpg', 'Logitech'),
(7, NULL, N'Pulsar X2H', N'Gaming Mouse', 2690000, 20, 'x2h.jpg', 'Pulsar'),
(7, NULL, N'Attack Shark X6', N'Gaming Mouse', 890000, 40, 'attacksharkx6.jpg', 'Attack Shark'),
(7, NULL, N'WLMouse Beast X Max', N'Gaming Mouse', 4290000, 15, 'beastxmax.jpg', 'WLMouse'),
(7, NULL, N'Lamzu Maya X', N'Gaming Mouse', 2990000, 20, 'mayax.jpg', 'Lamzu'),
(7, NULL, N'Endgame Gear OP1 8K', N'Gaming Mouse', 2690000, 20, 'op18k.jpg', 'Endgame'),
(7, NULL, N'Logitech G502 X Lightspeed', N'Gaming Mouse', 3890000, 15, 'g502x.jpg', 'Logitech'),
(7, NULL, N'Razer DeathAdder V4 Pro', N'Gaming Mouse', 3990000, 18, 'dav4pro.jpg', 'Razer'),
(8, NULL, N'Anker USB-C 100W', N'Cáp sạc nhanh', 390000, 100, 'anker100w.jpg', 'Anker'),
(8, NULL, N'Ugreen USB-C 100W', N'Cáp sạc', 290000, 120, 'ugreen100w.jpg', 'Ugreen'),
(8, NULL, N'Baseus USB-C PD', N'Cáp sạc nhanh', 190000, 150, 'baseuspd.jpg', 'Baseus'),
(8, NULL, N'Apple USB-C Cable', N'Cáp sạc Apple', 590000, 80, 'applecable.jpg', 'Apple'),
(8, NULL, N'Samsung Super Fast Cable', N'Cáp sạc Samsung', 250000, 90, 'samsungcable.jpg', 'Samsung'),
(8, NULL, N'Belkin BoostCharge USB-C', N'Cáp sạc 100W', 350000, 80, 'belkin100w.jpg', 'Belkin'),
(8, NULL, N'Anker Nano USB-C', N'Cáp sạc nhanh', 290000, 100, 'ankernano.jpg', 'Anker'),
(8, NULL, N'Choetech USB-C PD', N'Cáp sạc PD', 220000, 120, 'choetechpd.jpg', 'Choetech'),
(8, NULL, N'Essager USB-C 240W', N'Cáp sạc công suất cao', 180000, 150, 'essager240w.jpg', 'Essager'),
(8, NULL, N'McDodo USB-C Fast Charge', N'Cáp sạc nhanh', 260000, 100, 'mcdodo.jpg', 'McDodo');
GO

/*==========================================================
    KIỂM TRA DỮ LIỆU SAU KHỦNG CẤU TRÚC
==========================================================*/
SELECT 
    u.Id AS UserId,
    u.FullName,
    u.Email,
    r.Name AS RoleName
FROM Users u
JOIN Authorities a ON u.Id = a.UserId
JOIN Roles r ON a.RoleId = r.Id;
GO

-------------------------------
USE TheGioiDienTu;
GO

------------------------------------------------------------------
-- 1. Cập nhật mật khẩu chuẩn (lấy từ admin_test) cho admin & admin2
------------------------------------------------------------------
UPDATE Users 
SET Password = (SELECT TOP 1 Password FROM Users WHERE Email = 'admin_test@gmail.com')
WHERE Email IN ('admin@gmail.com', 'admin2@gmail.com');
GO

------------------------------------------------------------------
-- 2. Cấp quyền ROLE_ADMIN chuẩn (RoleId) cho admin@gmail.com
------------------------------------------------------------------
DELETE FROM Authorities WHERE UserId = (SELECT Id FROM Users WHERE Email = 'admin@gmail.com');

INSERT INTO Authorities (UserId, RoleId)
VALUES (
    (SELECT Id FROM Users WHERE Email = 'admin@gmail.com'),
    (SELECT Id FROM Roles WHERE Name = 'ROLE_ADMIN')
);
GO

------------------------------------------------------------------
-- 3. Cấp quyền ROLE_ADMIN chuẩn (RoleId) cho admin2@gmail.com
------------------------------------------------------------------
DELETE FROM Authorities WHERE UserId = (SELECT Id FROM Users WHERE Email = 'admin2@gmail.com');

INSERT INTO Authorities (UserId, RoleId)
VALUES (
    (SELECT Id FROM Users WHERE Email = 'admin2@gmail.com'),
    (SELECT Id FROM Roles WHERE Name = 'ROLE_ADMIN')
);
GO

------------------------------------------------------------------
-- 4. Kiểm tra lại danh sách các Admin trong hệ thống
------------------------------------------------------------------
SELECT u.Email, r.Name AS RoleName, u.Enabled
FROM Users u
JOIN Authorities a ON u.Id = a.UserId
JOIN Roles r ON a.RoleId = r.Id
WHERE u.Email IN ('admin@gmail.com', 'admin2@gmail.com', 'admin_test@gmail.com');
GO