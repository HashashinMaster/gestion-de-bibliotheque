# Système de Gestion de Bibliothèque

Une application JavaFX pour la gestion d'une bibliothèque, permettant de gérer les livres, les membres et les emprunts.

## Fonctionnalités

- **Gestion des Livres**: Ajouter, modifier, supprimer et rechercher des livres
- **Gestion des Membres**: Ajouter, modifier, supprimer et rechercher des membres
- **Gestion des Emprunts**: Ajouter, modifier, rechercher et retourner des emprunts
- **Interface utilisateur intuitive**: Navigation par onglets et formulaires simples
- **Base de données MySQL**: Stockage persistant des données

## Structure du Projet

Le projet suit l'architecture MVC (Modèle-Vue-Contrôleur) et le pattern DAO (Data Access Object):

- **Modèles**: Classes représentant les entités (Livre, Membre, Emprunt)
- **DAO**: Interfaces et implémentations pour l'accès aux données
- **Contrôleurs**: Gestion de la logique métier et des interactions utilisateur
- **Vues**: Interfaces utilisateur intégrées dans les contrôleurs

## Technologies Utilisées

- **JavaFX**: Framework d'interface utilisateur
- **MySQL**: Système de gestion de base de données relationnelle
- **Maven**: Gestion des dépendances et build
- **dotenv-java**: Gestion des variables d'environnement

## Installation et Exécution

### Prérequis

- Java JDK 11 ou supérieur
- Maven
- Serveur MySQL (port 3307, utilisateur root sans mot de passe)

### Étapes d'installation

1. Cloner le dépôt:
   ```
   git clone [URL_DU_REPO]
   ```

2. Naviguer vers le répertoire du projet:
   ```
   cd javafx_tp
   ```

3. Configurer les variables d'environnement:
   ```
   cp .env.example .env
   ```
   - Modifier ces valeurs selon votre configuration MySQL

4. Compiler le projet avec Maven:
   ```
   mvn clean package
   ```

5. Exécuter l'application:
   ```
   mvn clean javafx:run
   ```

## Structure de la Base de Données

### Table `livres`
- `id`: Identifiant unique du livre (INT, AUTO_INCREMENT)
- `titre`: Titre du livre (VARCHAR)
- `auteur`: Auteur du livre (VARCHAR)
- `isbn`: Numéro ISBN (VARCHAR, UNIQUE)
- `annee_publication`: Année de publication (INT)
- `editeur`: Maison d'édition (VARCHAR)
- `disponible`: Statut de disponibilité (TINYINT, 1 = disponible, 0 = emprunté)

### Table `membres`
- `id`: Identifiant unique du membre (INT, AUTO_INCREMENT)
- `nom`: Nom de famille (VARCHAR)
- `prenom`: Prénom (VARCHAR)
- `email`: Adresse email (VARCHAR, UNIQUE)
- `telephone`: Numéro de téléphone (VARCHAR)
- `adresse`: Adresse postale (VARCHAR)
- `date_inscription`: Date d'inscription (VARCHAR)

### Table `emprunts`
- `id`: Identifiant unique de l'emprunt (INT, AUTO_INCREMENT)
- `livre_id`: Référence au livre emprunté (INT, FOREIGN KEY)
- `membre_id`: Référence au membre emprunteur (INT, FOREIGN KEY)
- `date_emprunt`: Date de l'emprunt (VARCHAR)
- `date_retour_prevue`: Date prévue pour le retour (VARCHAR)
- `date_retour_reelle`: Date réelle du retour (VARCHAR, NULL si non retourné)



## Auteur

EL MAHDI BOUZKOURA

