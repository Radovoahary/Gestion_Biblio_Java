DROP DATABASE IF EXISTS bibliotheque_db;
CREATE DATABASE bibliotheque_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque_db;

-- =====================================================================
-- Table 1/4 : categories
-- Chaque livre appartient à une catégorie (relation 1-N)
-- =====================================================================
CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- =====================================================================
-- Table 2/4 : livres
-- =====================================================================
CREATE TABLE livres (
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    titre                    VARCHAR(200) NOT NULL,
    auteur                   VARCHAR(150) NOT NULL,
    isbn                     VARCHAR(20) UNIQUE,
    annee                    INT,
    exemplaires_disponibles  INT NOT NULL DEFAULT 0,
    categorie_id             INT,
    CONSTRAINT fk_livres_categorie
        FOREIGN KEY (categorie_id) REFERENCES categories(id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================================
-- Table 3/4 : membres
-- =====================================================================
CREATE TABLE membres (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL,
    email       VARCHAR(150) UNIQUE,
    type_membre ENUM('ETUDIANT', 'ENSEIGNANT') NOT NULL DEFAULT 'ETUDIANT'
) ENGINE=InnoDB;

-- =====================================================================
-- Table 4/4 : emprunts
-- Relie livres et membres. ON DELETE RESTRICT = empêche la suppression
-- d'un livre ou d'un membre tant qu'il a des emprunts liés (règle métier
-- "impossible de supprimer un livre/membre ayant des emprunts en cours",
-- appliquée ici au niveau base de données en complément du contrôle Java).
-- =====================================================================
CREATE TABLE emprunts (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    livre_id           INT NOT NULL,
    membre_id          INT NOT NULL,
    date_emprunt       DATE NOT NULL,
    date_retour_prevu  DATE NOT NULL,
    date_retour_reel   DATE NULL,
    CONSTRAINT fk_emprunts_livre
        FOREIGN KEY (livre_id) REFERENCES livres(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_emprunts_membre
        FOREIGN KEY (membre_id) REFERENCES membres(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Index utiles pour accélérer les futurs rapports statistiques (GROUP BY, JOIN)
CREATE INDEX idx_emprunts_livre   ON emprunts(livre_id);
CREATE INDEX idx_emprunts_membre  ON emprunts(membre_id);
CREATE INDEX idx_livres_categorie ON livres(categorie_id);

-- =====================================================================
-- Données de test
-- =====================================================================

INSERT INTO categories (nom, description) VALUES
('Roman',        'Fiction narrative'),
('Informatique', 'Livres techniques et programmation'),
('Histoire',     'Ouvrages historiques'),
('Sciences',     'Sciences naturelles et exactes'),
('Philosophie',  'Essais et pensée philosophique');

INSERT INTO livres (titre, auteur, isbn, annee, exemplaires_disponibles, categorie_id) VALUES
('Le Petit Prince',            'Antoine de Saint-Exupéry', '9782070408504', 1943, 3, 1),
('1984',                       'George Orwell',            '9782070368228', 1949, 2, 1),
('Clean Code',                 'Robert C. Martin',         '9780132350884', 2008, 4, 2),
('Effective Java',             'Joshua Bloch',              '9780134685991', 2018, 2, 2),
('Design Patterns',            'Gang of Four',              '9780201633610', 1994, 2, 2),
('Sapiens',                    'Yuval Noah Harari',         '9782226257017', 2015, 3, 3),
('Une Brève Histoire du Temps','Stephen Hawking',           '9782081338994', 1988, 1, 4),
('Le Mythe de Sisyphe',        'Albert Camus',               '9782070322882', 1942, 0, 5);

INSERT INTO membres (nom, email, type_membre) VALUES
('Rakoto Andry',        'rakoto.andry@univ.mg',  'ETUDIANT'),
('Rasoa Miora',         'rasoa.miora@univ.mg',   'ETUDIANT'),
('Prof. Randria Jean',  'j.randria@univ.mg',     'ENSEIGNANT'),
('Rabe Solo',           'rabe.solo@univ.mg',     'ETUDIANT'),
('Prof. Ravaka Nirina', 'n.ravaka@univ.mg',      'ENSEIGNANT'),
('Fanja Hery',          'fanja.hery@univ.mg',    'ETUDIANT');

-- Emprunts de test : un rendu, plusieurs en cours, et UN en retard volontaire
-- (livre_id=3 "Clean Code", retour prévu le 15/06/2026, jamais rendu)
-- pour pouvoir tester tout de suite la mise en évidence des retards.
INSERT INTO emprunts (livre_id, membre_id, date_emprunt, date_retour_prevu, date_retour_reel) VALUES
(1, 1, '2026-06-20', '2026-07-04', '2026-07-02'),  -- rendu à temps
(2, 1, '2026-07-01', '2026-07-15', NULL),          -- en cours, pas encore en retard
(3, 3, '2026-06-01', '2026-06-15', NULL),          -- EN RETARD
(8, 5, '2026-06-25', '2026-07-09', NULL),          -- en cours (dernier exemplaire du livre 8)
(4, 2, '2026-07-05', '2026-07-19', NULL);          -- en cours