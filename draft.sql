-- execture this first and refresh the schemas
CREATE DATABASE HOTELTHEATERSHOWSDRAFT;

-- double click hoteltheatershowsdraft and execute these and refresh the schemas
CREATE TABLE theaters (
	theaterid int NOT NULL AUTO_INCREMENT,
    capacity int NOT NULL,
    layout varchar(20) NOT NULL,
    status varchar (10) NOT NULL,
    availble_date date NOT NULL,
    available_time time NOT NULL,
    PRIMARY KEY (theaterid)
);

CREATE TABLE shows (
	showid int NOT NULL AUTO_INCREMENT,
	title varchar(50) NOT NULL,
	runtime varchar(50) NOT NULL,
	starttime time NOT NULL,
    endtime time NOT NULL,
    theaterhall int NOT NULL,
    showstatus varchar (15) NOT NULL,
	PRIMARY KEY (showid),
    FOREIGN KEY (theaterhall) REFERENCES theaters (theaterid)
);

CREATE TABLE customers (
	customerid int NOT NULL AUTO_INCREMENT,
    first_name varchar (50) NOT NULL,
    last_name varchar (50) NOT NULL,
    phone_number int NOT NULL,
    email_address varchar (50) NOT NULL,
    assigned_theater int NOT NULL,
    show_watched int NOT NULL,
    PRIMARY KEY (customerid),
    FOREIGN KEY (assigned_theater) REFERENCES theaters (theaterid),
    FOREIGN KEY (show_watched) REFERENCES shows (showid)
);

CREATE TABLE staff (
	staffid int NOT NULL AUTO_INCREMENT,
    first_name varchar (50) NOT NULL,
    last_name varchar (50) NOT NULL,
    position varchar (10) NOT NULL,
    status varchar (10) NOT NULL,
    shift varchar (10) NOT NULL,
    salary decimal NOT NULL,
    station int NOT NULL,
    customer_served int NOT NULL,
    PRIMARY KEY (staffid),
    FOREIGN KEY (station) REFERENCES theaters (theaterid),
    FOREIGN KEY (customer_served) REFERENCES customers (customerid)
);

-- Go to database tab, click reverse engineer and just select next until finish