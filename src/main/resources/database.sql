-- Script de création de la base de données pour le système de gestion de bibliothèque
-- Tables: livres, membres, emprunts

-- Table des livres
CREATE TABLE IF NOT EXISTS livres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    annee_publication INT,
    editeur VARCHAR(255),
    disponible TINYINT(1) DEFAULT 1
);

-- Table des membres
CREATE TABLE IF NOT EXISTS membres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    date_inscription VARCHAR(10) NOT NULL
);

-- Table des emprunts
CREATE TABLE IF NOT EXISTS emprunts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    livre_id INT NOT NULL,
    membre_id INT NOT NULL,
    date_emprunt VARCHAR(10) NOT NULL,
    date_retour_prevue VARCHAR(10) NOT NULL,
    date_retour_reelle VARCHAR(10),
    FOREIGN KEY (livre_id) REFERENCES livres(id),
    FOREIGN KEY (membre_id) REFERENCES membres(id)
);

-- Insertion de données d'exemple
INSERT INTO livres (titre, auteur, isbn, annee_publication, editeur, disponible)
VALUES 
    ('Le Petit Prince', 'Antoine de Saint-Exupéry', '9782070612758', 1943, 'Gallimard', 1),
    ('1984', 'George Orwell', '9782070368228', 1949, 'Gallimard', 1),
    ('L''Étranger', 'Albert Camus', '9782070360024', 1942, 'Gallimard', 1),
    ('Candide', 'Voltaire', '9782081358133', 1759, 'Flammarion', 1),
    ('Les Misérables', 'Victor Hugo', '9782253096344', 1862, 'Le Livre de Poche', 1);

INSERT INTO membres (nom, prenom, email, telephone, adresse, date_inscription)
VALUES
    ('Dupont', 'Jean', 'jean.dupont@email.com', '0123456789', '1 rue de Paris, 75001 Paris', '2023-01-15'),
    ('Martin', 'Sophie', 'sophie.martin@email.com', '0234567890', '2 avenue des Champs-Élysées, 75008 Paris', '2023-02-20'),
    ('Dubois', 'Pierre', 'pierre.dubois@email.com', '0345678901', '3 boulevard Saint-Michel, 75005 Paris', '2023-03-10'),
    ('Lefebvre', 'Marie', 'marie.lefebvre@email.com', '0456789012', '4 rue de Rivoli, 75004 Paris', '2023-04-05'),
    ('Bernard', 'Thomas', 'thomas.bernard@email.com', '0567890123', '5 place de la Bastille, 75011 Paris', '2023-05-12');
