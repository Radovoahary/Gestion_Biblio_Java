-- 1. Création de la base de données
CREATE DATABASE IF NOT EXISTS bibliotheque_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque_db;

-- 2. Suppression des tables si elles existent
DROP TABLE IF EXISTS emprunts;
DROP TABLE IF EXISTS membres;
DROP TABLE IF EXISTS livres;
DROP TABLE IF EXISTS categories;

-- 3. Création de la table Catégories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Création de la table Livres
CREATE TABLE livres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    annee INT NOT NULL,
    exemplaires_disponibles INT NOT NULL DEFAULT 0,
    categorie_id INT,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 5. Création de la table Membres
CREATE TABLE membres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    type_membre ENUM('ETUDIANT', 'ENSEIGNANT') NOT NULL
);

-- 6. Création de la table Emprunts
CREATE TABLE emprunts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    membre_id INT NOT NULL,
    livre_id INT NOT NULL,
    date_emprunt DATE NOT NULL,
    date_retour_prevu DATE NOT NULL,
    date_retour_reel DATE NULL,
    FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE CASCADE,
    FOREIGN KEY (livre_id) REFERENCES livres(id) ON DELETE CASCADE
);

-- 7. Insertion de données de test
INSERT INTO categories (nom) VALUES ('Informatique'), ('Sciences'), ('Littérature'), ('Histoire');

INSERT INTO livres (titre, auteur, isbn, annee, exemplaires_disponibles, categorie_id) VALUES
('Java Approfondi', 'Jean Dev', '978-1234567890', 2024, 5, 1),
('Design Patterns', 'Erich Gamma', '978-0201633610', 1994, 2, 1),
('Physique Quantique', 'Marie Curie', '978-9876543210', 2021, 3, 2);

INSERT INTO membres (nom, email, type_membre) VALUES
('Alice Dupont', 'alice.dupont@univ.fr', 'ETUDIANT'),
('Prof. Martin', 'pierre.martin@univ.fr', 'ENSEIGNANT');

INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevu, date_retour_reel) VALUES
(1, 1, '2026-06-20', '2026-07-04', NULL), -- En retard (si on est après le 04/07)
(2, 2, '2026-07-01', '2026-07-15', NULL);