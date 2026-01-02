CREATE DATABASE IF NOT EXISTS Group16;
USE Group16;

CREATE TABLE UserInfo (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
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
    invoice_pdf LONGBLOB,
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

INSERT INTO UserInfo (username, password, role, address)
VALUES
    ('cust','80d26609c5226268981e4a6d4ceddbc339d991841ae580e3180b56c8ade7651d','customer','Istanbul'),
    ('carr','f9356b0952e5681f9bb4969078d6762f1f3f3eb9e87b80d6544103ad918f074c','carrier','Istanbul'),
    ('own','5b3975651c3cab92d044c096dc30a1c2d9525497457472de48c51ecb363d1f4a','owner','Istanbul'),
    ('cust1','054de311ed040be47336f105190751aaed629b7e127593c2bde5d97229d8389e','customer','Kadikoy'),
    ('cust2','ced9bb677a35528709430b1df4dabc97535994193c0ca53247a819d36b3cb344','customer','Besiktas'),
    ('cust3','bd6192fc7f55744a64c44115826d654505653b2a076826b9bbc3d720043cd5e9','customer','Sisli');

INSERT INTO ProductInfo (name, type, price, stock, threshold, image_data)
VALUES
    ('Apple','fruit',25,100,5,NULL),
    ('Banana','fruit',18,80,5,NULL),
    ('Orange','fruit',20,70,5,NULL),
    ('Strawberry','fruit',45,40,3,NULL),
    ('Grape','fruit',30,60,4,NULL),
    ('Pear','fruit',22,50,4,NULL),
    ('Peach','fruit',28,45,3,NULL),
    ('Cherry','fruit',55,30,2,NULL),
    ('Pineapple','fruit',60,25,2,NULL),
    ('Kiwi','fruit',35,40,3,NULL),
    ('Mango','fruit',50,35,3,NULL),
    ('Watermelon','fruit',10,200,20,NULL),
    ('Tomato','vegetable',15,90,10,NULL),
    ('Potato','vegetable',10,120,5,NULL),
    ('Onion','vegetable',8,150,5,NULL),
    ('Cucumber','vegetable',12,70,5,NULL),
    ('Pepper','vegetable',20,60,4,NULL),
    ('Carrot','vegetable',9,80,5,NULL),
    ('Eggplant','vegetable',14,50,4,NULL),
    ('Zucchini','vegetable',13,55,4,NULL),
    ('Spinach','vegetable',11,40,3,NULL),
    ('Broccoli','vegetable',18,35,3,NULL),
    ('Cauliflower','vegetable',17,30,3,NULL),
    ('Lettuce','vegetable',7,60,5,NULL);

INSERT INTO Coupons (code, discount_amount, expired_date, is_active)
VALUES
    ('WELCOME10', 10, '2026-01-31', 1),
    ('LOYAL20', 20, '2026-06-30', 1),
    ('SPRING15', 15, '2026-04-30', 1),
    ('VIP25', 25, '2026-12-31', 1),
    ('Bonus', 99, '2026-02-02', 0),
    ('OLD5', 5, '2024-12-01', 0);

INSERT INTO UserCoupons (userID, couponID, is_used)
VALUES
    (1, 1, 0),
    (1, 2, 0);

INSERT INTO OrderInfo (customerID, carrierID, delivery_date, status, total_cost, invoice_data, carrier_rating)
VALUES (1, NULL, NULL, 'Pending', 180.50, NULL, NULL);

INSERT INTO OrderItems (orderID, productID, amount, unit_price)
VALUES
    (1, 1, 2.0, 50.00),
    (1, 7, 3.5, 52.50);

INSERT INTO Messages (senderID, receiverID, content)
VALUES
    (1, 3, 'Merhaba, siparişim ne zaman teslim edilecek?'),
    (3, 1, 'Merhaba, siparişiniz bugün içerisinde hazırlanacaktır.');