-- Local TEST database created only for this assignment  (MySQL 8)
-- Run:  mysql -u root -p < schema.sql
CREATE DATABASE IF NOT EXISTS busdb;
USE busdb;

CREATE TABLE IF NOT EXISTS bus_pass (
    pass_id  VARCHAR(10) PRIMARY KEY,
    reg_no   VARCHAR(12) NOT NULL,
    name     VARCHAR(60) NOT NULL,
    route_no INT         NOT NULL,
    amount   DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (          -- Task B2 (login)
    id  VARCHAR(30) PRIMARY KEY,
    pwd VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS wallet (         -- Task A5 / transactions
    owner   VARCHAR(30) PRIMARY KEY,
    balance DECIMAL(10,2) NOT NULL
);
