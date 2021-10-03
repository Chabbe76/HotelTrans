Drop database hoteltransylvania;

CREATE DATABASE IF NOT EXISTS hotelTransylvania;

use hoteltransylvania;

CREATE TABLE IF NOT EXISTS customers(
customerId INT NOT NULL AUTO_INCREMENT,
firstName VARCHAR(255),
lastName VARCHAR(255),
PRIMARY KEY(customerId)
);

INSERT INTO customers (firstName, lastName)
VALUES ('Robert', 'Tenglund'),
('Mio', 'Tholerus'),
('Suayb', 'Ugan');



CREATE TABLE IF	NOT EXISTS rooms(
roomId INT NOT NULL AUTO_INCREMENT,
roomSize VARCHAR(255),
roomCategory VARCHAR(255),
available bit not null default true,
PRIMARY KEY(roomId)
);

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Double', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Double', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Double', 'Standard');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Deluxe');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Single', 'Deluxe');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Double', 'Deluxe');

INSERT INTO rooms (roomSize, roomCategory)
VALUES ('Double', 'Deluxe');



CREATE TABLE IF NOT EXISTS food(
foodId INT NOT NULL AUTO_INCREMENT,
foodName VARCHAR(255),
foodType VARCHAR(255),
foodPrice INT,
PRIMARY KEY(foodId)
);

INSERT INTO food (foodName, foodType, foodPrice)
VALUES ('Pizza', 'Food', 110),
('Pasta', 'Food', 100),
('Salad', 'Food', 85),
('Steak', 'Food', 150),
('Lentil Soup', 'Food', 60),
('Pancakes', 'Food', 50),
('Fries', 'Food', 35),
('Nuts', 'Food', 30),
('Soft Drink', 'Drink', 15),
('Beer', 'Drink', 50),
('Wine', 'Drink', 80),
('Cider', 'Drink', 60),
('Coffee', 'Drink', 25),
('Tea', 'Drink', 25),
('Jolt Cola', 'Drink', 20);




-- Ersätter customers_rooms och rooms_food
CREATE TABLE IF NOT EXISTS booking (
	bookingId int PRIMARY KEY AUTO_INCREMENT,
    customerId int NOT NULL,
    roomId int NOT NULL,
    checkinDate DATETIME NOT NULL DEFAULT NOW(), -- Nu om en inte anger något annat
    checkoutDate DATETIME DEFAULT (NOW() + INTERVAL 1 DAY),
    -- bill int DEFAULT 0,
    FOREIGN KEY (customerId) REFERENCES customers(customerId),
    FOREIGN KEY (roomId) REFERENCES rooms(roomId)
);

INSERT INTO booking (customerId, roomId) VALUES
	(1, 4),
	(2, 3),
	(3, 5);
UPDATE rooms SET available = false WHERE roomId IN(4, 3, 5);



CREATE TABLE IF NOT EXISTS booking_food (
	bookingId int NOT NULL,
    foodId int NOT NULL,
    FOREIGN KEY (bookingId) REFERENCES booking(bookingId),
    FOREIGN KEY (foodId) REFERENCES food(foodId)
    -- PRIMARY KEY (bookingId, foodId)
);



-- SELECT * FROM customers;

-- SELECT * FROM booking;

-- SELECT * FROM rooms;

-- SELECT * FROM food;



CREATE VIEW allinfo AS
SELECT customers.customerId, firstName, lastName, booking.bookingId, rooms.roomId, checkinDate, checkoutDate, food.foodName, foodPrice FROM customers
	LEFT JOIN booking ON customers.customerId = booking.customerId
    LEFT JOIN rooms ON rooms.roomId = booking.roomId
	LEFT JOIN booking_food ON booking_food.bookingId = booking.bookingId
    LEFT JOIN food ON food.foodId = booking_food.foodId;

SELECT * FROM allinfo;



-- Om du vill testa checka ut med någon som checkade in idag, och därför bara har noll dagar - kör den här:
-- UPDATE booking SET checkInDate = (NOW() - INTERVAL 4 DAY) WHERE customerId = 2;







