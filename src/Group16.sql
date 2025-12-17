CREATE DATABASE IF NOT EXISTS Group16;
USE Group16;

CREATE TABLE UserInfo (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('customer', 'carrier', 'owner') NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    address VARCHAR(255),
    phone_number VARCHAR(20),
    loyalty_points INT DEFAULT 0
);

CREATE TABLE ProductInfo (
    productID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type ENUM('fruit', 'vegetable') NOT NULL,
    price DOUBLE NOT NULL,
    stock DOUBLE NOT NULL,
    threshold DOUBLE DEFAULT 5.0,
    image_data LONGBLOB
);

CREATE TABLE OrderInfo (
    orderID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT NOT NULL,
    carrierID INT,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    delivery_date DATETIME,
    status ENUM('Pending', 'Selected', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    total_cost DOUBLE NOT NULL,
    invoice_data LONGTEXT,
    carrier_rating INT CHECK (carrier_rating BETWEEN 1 AND 5),
    FOREIGN KEY (customerID) REFERENCES UserInfo(userID),
    FOREIGN KEY (carrierID) REFERENCES UserInfo(userID)
);

CREATE TABLE OrderItems (
    orderItemID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    amount DOUBLE NOT NULL,
    unit_price DOUBLE NOT NULL,
    FOREIGN KEY (orderID) REFERENCES OrderInfo(orderID) ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES ProductInfo(productID)
);

CREATE TABLE Messages (
    messageID INT AUTO_INCREMENT PRIMARY KEY,
    senderID INT NOT NULL,
    receiverID INT NOT NULL,
    content TEXT NOT NULL,
    sent_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (senderID) REFERENCES UserInfo(userID),
    FOREIGN KEY (receiverID) REFERENCES UserInfo(userID)
);

CREATE TABLE Coupons (
    couponID INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    discount_amount DOUBLE NOT NULL,
    expired_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE UserCoupons (
    userCouponID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    couponID INT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    assigned_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES UserInfo(userID),
    FOREIGN KEY (couponID) REFERENCES Coupons(couponID)
);