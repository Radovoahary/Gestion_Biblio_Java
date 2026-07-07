CREATE DATABASE IF NOT EXISTS bibliotheque_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque_db;
-- Suppression des tables s'il y en a déjà
DROP TABLE IF EXISTS emprunts;
DROP TABLE IF EXISTS membres;
DROP TABLE IF EXISTS livres;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE;
);
