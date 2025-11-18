CREATE DATABASE IF NOT EXISTS `THEATERSHOWS`;
USE `THEATERSHOWS`;

CREATE TABLE `payment` (
  `PAYMENT_ID` VARCHAR(8),
  `AMOUNT` DECIMAL,
  `PAYMENT_STATUS` VARCHAR(50),
  `PAYMENT_DATE` DATE,
  PRIMARY KEY (`PAYMENT_ID`)
);

CREATE TABLE `customers` (
  `CUSTOMER_ID` VARCHAR(8),
  `FIRST_NAME` VARCHAR(50),
  `LAST_NAME` VARCHAR(50),
  `PHONE_NUMBER` VARCHAR(50),
  `EMAIL_ADDRESS` VARCHAR(50),
  PRIMARY KEY (`CUSTOMER_ID`)
);

CREATE TABLE `theaters` (
  `THEATER_ID` VARCHAR(8),
  `THEATER_NAME` VARCHAR(20),
  `CAPACITY` INT,
  PRIMARY KEY (`THEATER_ID`)
);

CREATE TABLE `theater_reservation` (
  `THEATER_RESERVATION_ID` VARCHAR(8) ,
  `THEATER_ID` VARCHAR(8),
  `RESERVED_DATE` DATE,
  `RESERVATION_STATUS` VARCHAR(20),
  PRIMARY KEY (THEATER_RESERVATION_ID),
  FOREIGN KEY (THEATER_ID) 
	  REFERENCES theaters(THEATER_ID)
);

CREATE TABLE `shows` (
  `SHOW_ID` VARCHAR(8),
  `TITLE` VARCHAR(50),
  `RUNTIME` TIME,
  `SHOW_PRICE` INT,
  `STATUS` VARCHAR(50),
  PRIMARY KEY (SHOW_ID)
);

CREATE TABLE `theater_shows` (
  `THEATER_SHOW_ID` VARCHAR(8),
  `THEATER_RESERVATION_ID` VARCHAR(8) UNIQUE,
  `SHOW_ID` VARCHAR(8),
  `START_TIME` TIME,
  `END_TIME` TIME,
  `SHOW_STATUS` VARCHAR(50),
  `AUDIENCE_TURNOUT` DECIMAL,
  PRIMARY KEY (THEATER_SHOW_ID),
  FOREIGN KEY (THEATER_RESERVATION_ID)
      REFERENCES theater_reservation(THEATER_RESERVATION_ID),
  FOREIGN KEY (SHOW_ID)
      REFERENCES shows(SHOW_ID)
);

CREATE TABLE `booking` (
  `BOOKING_ID` VARCHAR(8),
  `CUSTOMER_ID` VARCHAR(8) UNIQUE,
  `THEATER_SHOW_ID` VARCHAR(8) UNIQUE,
  `PAYMENT_ID` VARCHAR(8) UNIQUE,
  `NOOFTICKETS` INT,
  `TOTAL_PRICE` INT,
  `BOOKING_STATUS` VARCHAR(50),
  `BOOKING_DATE` DATE,
  PRIMARY KEY (BOOKING_ID),
  FOREIGN KEY (CUSTOMER_ID)
      REFERENCES customers(CUSTOMER_ID),
  FOREIGN KEY (PAYMENT_ID)
      REFERENCES payment(PAYMENT_ID),
  FOREIGN KEY (THEATER_SHOW_ID)
      REFERENCES theater_shows(THEATER_SHOW_ID)
);

CREATE TABLE `seat` (
  `SEAT_ID` VARCHAR(8),
  `THEATER_ID` VARCHAR(8),
  `ROW_NO` INT,
  `COL_NO` INT,
  `STATUS` VARCHAR(50),
  PRIMARY KEY (SEAT_ID),
  FOREIGN KEY (THEATER_ID)
      REFERENCES theaters(THEATER_ID)
);

CREATE TABLE `seat_booking` (
  `BOOKING_ID` VARCHAR(8),
  `SEAT_ID` VARCHAR(8) UNIQUE,
  PRIMARY KEY (BOOKING_ID, SEAT_ID),
  FOREIGN KEY (BOOKING_ID)
      REFERENCES booking(BOOKING_ID),
  FOREIGN KEY (SEAT_ID)
      REFERENCES seat(SEAT_ID)
);

CREATE TABLE `staff` (
  `STAFF_ID` VARCHAR(8),
  `FIRST_NAME` VARCHAR(50),
  `LAST_NAME` VARCHAR(50),
  `POSITION` VARCHAR(50),
  `EMPLOYMENT_STATUS` VARCHAR(50),
  `SALARY` INT,
  PRIMARY KEY (STAFF_ID)
);

CREATE TABLE `staff_assignment` (
  `STAFF_ID` VARCHAR(8),
  `THEATER_SHOW_ID` VARCHAR(8),
  PRIMARY KEY (STAFF_ID, THEATER_SHOW_ID),
  FOREIGN KEY (STAFF_ID)
      REFERENCES staff(STAFF_ID),
  FOREIGN KEY (THEATER_SHOW_ID)
      REFERENCES theater_shows(THEATER_SHOW_ID)
);

-- for customers
DELIMITER //

CREATE TRIGGER before_insert_customers
BEFORE INSERT ON customers
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest CUSTOMER_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(CUSTOMER_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM customers;

    -- Generate the new CUSTOMER_ID
    SET NEW.CUSTOMER_ID = CONCAT('CT', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for booking
DELIMITER //

CREATE TRIGGER before_insert_booking
BEFORE INSERT ON booking
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest BOOKING_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(BOOKING_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM booking;

    -- Generate the new BOOKING_ID
    SET NEW.BOOKING_ID = CONCAT('BK', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for theater show
DELIMITER //

CREATE TRIGGER before_insert_theater_shows
BEFORE INSERT ON theater_shows
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest THEATER_SHOW_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(THEATER_SHOW_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM theater_shows;

    -- Generate the new THEATER_SHOW_ID
    SET NEW.THEATER_SHOW_ID = CONCAT('TS', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for payment
DELIMITER //

CREATE TRIGGER before_insert_payment
BEFORE INSERT ON payment
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest PAYMENT_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(PAYMENT_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM payment;

    -- Generate the new PAYMENT_ID
    SET NEW.PAYMENT_ID = CONCAT('PM', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for theater reservation ID
DELIMITER //

CREATE TRIGGER before_insert_theater_reservation
BEFORE INSERT ON theater_reservation
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest THEATER_RESERVATION_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(THEATER_RESERVATION_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM theater_reservation;

    -- Generate the new THEATER_RESERVATION_ID
    SET NEW.THEATER_RESERVATION_ID = CONCAT('TR', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for show id 
DELIMITER //

CREATE TRIGGER before_insert_shows
BEFORE INSERT ON shows
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest SHOW_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(SHOW_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM shows;

    -- Generate the new SHOW_ID
    SET NEW.SHOW_ID = CONCAT('SH', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for theater id
DELIMITER //

CREATE TRIGGER before_insert_theaters
BEFORE INSERT ON theaters
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest THEATER_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(THEATER_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM theaters;

    -- Generate the new THEATER_ID
    SET NEW.THEATER_ID = CONCAT('TH', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for seat id 
DELIMITER //

CREATE TRIGGER before_insert_seat
BEFORE INSERT ON seat
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest SEAT_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(SEAT_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM seat;

    -- Generate the new SEAT_ID
    SET NEW.SEAT_ID = CONCAT('SE', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for staff id
DELIMITER //

CREATE TRIGGER before_insert_staff
BEFORE INSERT ON staff
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest STAFF_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(STAFF_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM staff;

    -- Generate the new STAFF_ID
    SET NEW.STAFF_ID = CONCAT('ST', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for seat id in seat booking
DELIMITER //

CREATE TRIGGER before_insert_seat_booking
BEFORE INSERT ON seat_booking
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest SEAT_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(SEAT_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM seat_booking;

    -- Generate the new SEAT_ID
    SET NEW.SEAT_ID = CONCAT('SE', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

-- for staff id in staff assignment
DELIMITER //

CREATE TRIGGER before_insert_staff_assignment
BEFORE INSERT ON staff_assignment
FOR EACH ROW
BEGIN
    DECLARE max_id INT;

    -- Get the numeric part of the largest STAFF_ID
    SELECT IFNULL(MAX(CAST(SUBSTRING(STAFF_ID, 3) AS UNSIGNED)), 0)
    INTO max_id
    FROM staff_assignment;

    -- Generate the new STAFF_ID
    SET NEW.STAFF_ID = CONCAT('ST', LPAD(max_id + 1, 6, '0'));
END //

DELIMITER ;

INSERT INTO customers (FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL_ADDRESS)
VALUES
('Daniel', 'Reyes', '09764821934', 'danny.r.codes@gmail.com'),
('Maria', 'Santos', '09157740298', 'sunnyorchid72@yahoo.com'),
('Adrian', 'Velasco', '09923184470', 'avelasco.mail@gmail.com'),
('Helena', 'Gutierrez', '09086615042', 'crystalwave019@yahoo.com'),
('Kenji', 'Nakamura', '09632458197', 'kenji.nkmr55@gmail.com'),
('Bianca', 'Torres', '09275531006', 'midnightbloom44@yahoo.com'),
('Marcus', 'Delgado', '09387402661', 'marcus.dlg310@gmail.com'),
('Soo-min', 'Kim', '09518894375', 'silvercrest89@yahoo.com'),
('Celeste', 'Alvarado', '09403675288', 'celeste.a103@gmail.com'),
('Rafael', 'Hernandez', '09972149063', 'emberline992@yahoo.com');

INSERT INTO staff (FIRST_NAME, LAST_NAME, POSITION, EMPLOYMENT_STATUS, SALARY)
VALUES
('Marcus', 'Reyes', 'Show Manager', 'ACTIVE', 45000),
('Emily', 'Flores', 'Stage Manager', 'ACTIVE', 40000),
('Jonathan', 'Morales', 'Sound Technician', 'ACTIVE', 32000),
('Victoria', 'Santos', 'Lighting Technician', 'ACTIVE', 32000),
('Daniel', 'Cruz', 'Ticketing/Box Office', 'ACTIVE', 25000),
('Isabelle', 'Navarro', 'Usher/Floor Staff', 'ACTIVE', 22000),
('Adrian', 'Delgado', 'Stage Coordinator', 'ACTIVE', 35000),
('Sophia', 'Rivera', 'Props/Set Designer', 'ACTIVE', 33000),
('Nathan', 'Gutierrez', 'Costume/Wardrobe Staff', 'ACTIVE', 30000),
('Olivia', 'Kim', 'Sound Assistant', 'ACTIVE', 28000);

INSERT INTO shows (TITLE, RUNTIME, SHOW_PRICE, STATUS)
VALUES
('Hamilton', '2:55:00', 8500, 'Ongoing'),
('Les Misérables', '2:50:00', 4000, 'Ongoing'),
('Dear Evan Hansen', '2:30:00', 3200, 'Ongoing'),
('Mamma Mia', '2:30:00', 5600, 'Ongoing'),
('Lion King', '2:30:00', 4125, 'Ongoing');

INSERT INTO theaters (THEATER_NAME, CAPACITY)
VALUES
('MAGALANG', 500),
('MATALINO', 300),
('MAKABAYAN', 1350);

INSERT INTO theater_reservation (RESERVED_DATE, RESERVATION_STATUS)
VALUES
('2026-02-05', 'RESERVED'),
('2026-02-06', 'RESERVED'),
('2026-02-07', 'RESERVED'),
('2026-02-05', 'RESERVED'),
('2026-02-06', 'RESERVED'),
('2026-02-05', 'RESERVED'),
('2026-02-06', 'RESERVED'),
('2026-02-07', 'RESERVED'),
('2026-02-08', 'RESERVED'),
('2026-02-09', 'RESERVED');

INSERT INTO theater_shows (START_TIME, END_TIME, SHOW_STATUS, AUDIENCE_TURNOUT)
VALUES
('11:00:00', '14:00:00', 'SCHEDULED', 0),
('18:00:00', '21:00:00', 'SCHEDULED', 0),
('13:30:00', '16:30:00', 'SCHEDULED', 0),
('13:00:00', '16:00:00', 'SCHEDULED', 0),
('13:00:00', '16:00:00', 'SCHEDULED', 0),
('10:30:00', '13:00:00', 'SCHEDULED', 0),
('19:00:00', '21:30:00', 'SCHEDULED', 0),
('19:00:00', '21:30:00', 'SCHEDULED', 0),
('15:00:00', '17:30:00', 'SCHEDULED', 0),
('15:00:00', '17:30:00', 'SCHEDULED', 0);

INSERT INTO seat (ROW_NO, COL_NO, STATUS)
VALUES
(2, 2, 'TAKEN'),
(2, 3, 'TAKEN'),
(2, 4, 'TAKEN'),
(15, 9, 'TAKEN'),
(15, 10, 'TAKEN'),
(1, 1, 'TAKEN'),
(1, 2, 'TAKEN'),
(1, 3, 'TAKEN'),
(1, 6, 'TAKEN'),
(6, 1, 'TAKEN'),
(6, 2, 'TAKEN'),
(7, 1, 'TAKEN'),
(7, 2, 'TAKEN'),
(11, 8, 'TAKEN'),
(3, 7, 'TAKEN'),
(3, 8, 'TAKEN'),
(3, 9, 'TAKEN'),
(4, 7, 'TAKEN'),
(4, 8, 'TAKEN'),
(10, 2, 'TAKEN'),
(10, 3, 'TAKEN'),
(11, 2, 'TAKEN'),
(11, 3, 'TAKEN'),
(20, 5, 'TAKEN'),
(20, 6, 'TAKEN'),
(25, 10, 'TAKEN'),
(28, 12, 'TAKEN'),
(28, 13, 'TAKEN'),
(29, 12, 'TAKEN'),
(29, 13, 'TAKEN');

INSERT INTO payment (AMOUNT, PAYMENT_STATUS, PAYMENT_DATE)
VALUES
(25500, 'PAID', '2025-11-20'),
(17000, 'PAID', '2025-11-20'),
(34000, 'PAID', '2025-11-20'),
(16000, 'UNPAID', null),
(4000,  'UNPAID', null),
(16000, 'PAID', '2025-11-20'),
(12800, 'PAID', '2025-11-20'),
(11200, 'PAID', '2025-11-20'),
(4125,  'PAID', '2025-11-20'),
(16500, 'PAID', '2025-11-20');


INSERT INTO booking (NOOFTICKETS, TOTAL_PRICE, BOOKING_STATUS, BOOKING_DATE)
VALUES
(3, 25500, 'CONFIRMED', '2025-11-20'),
(2, 17000, 'CONFIRMED', '2025-11-20'),
(4, 34000, 'CONFIRMED', '2025-11-20'),
(4, 16000, 'PENDING',   '2025-11-20'),
(1, 4000,  'PENDING',   '2025-11-20'),
(5, 16000, 'CONFIRMED', '2025-11-20'),
(4, 12800, 'CONFIRMED', '2025-11-20'),
(2, 11200, 'CONFIRMED', '2025-11-20'),
(1, 4125,  'CONFIRMED', '2025-11-20'),
(4, 16500, 'CONFIRMED', '2025-11-20');

INSERT INTO seat_booking (BOOKING_ID)
VALUES
('BK000001'),
('BK000001'),
('BK000001'),
('BK000002'),
('BK000002'),
('BK000003'),
('BK000003'),
('BK000003'),
('BK000003'),
('BK000004'),
('BK000004'),
('BK000004'),
('BK000004'),
('BK000005'),
('BK000006'),
('BK000006'),
('BK000006'),
('BK000006'),
('BK000006'),
('BK000007'),
('BK000007'),
('BK000007'),
('BK000007'),
('BK000008'),
('BK000008'),
('BK000009'),
('BK000010'),
('BK000010'),
('BK000010'),
('BK000010');

INSERT INTO staff_assignment (THEATER_SHOW_ID)
VALUES
('TS000001'),
('TS000002'),
('TS000003'),
('TS000004'),
('TS000005'),
('TS000006'),
('TS000007'),
('TS000008'),
('TS000009'),
('TS000010');
