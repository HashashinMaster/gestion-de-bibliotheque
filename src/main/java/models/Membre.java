package models;

import javafx.beans.property.*;

/**
 * Classe représentant un membre de la bibliothèque.
 */
public class Membre {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty telephone = new SimpleStringProperty();
    private final StringProperty adresse = new SimpleStringProperty();
    private final StringProperty dateInscription = new SimpleStringProperty();

    /**
     * Constructeur par défaut.
     */
    public Membre() {
    }

    /**
     * Constructeur avec paramètres.
     * 
     * @param id Identifiant unique du membre
     * @param nom Nom de famille du membre
     * @param prenom Prénom du membre
     * @param email Adresse email du membre
     * @param telephone Numéro de téléphone du membre
     * @param adresse Adresse postale du membre
     * @param dateInscription Date d'inscription du membre
     */
    public Membre(int id, String nom, String prenom, String email, String telephone, String adresse, String dateInscription) {
        this.id.set(id);
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.email.set(email);
        this.telephone.set(telephone);
        this.adresse.set(adresse);
        this.dateInscription.set(dateInscription);
    }

    /**
     * Constructeur sans ID pour la création de nouveaux membres.
     * 
     * @param nom Nom de famille du membre
     * @param prenom Prénom du membre
     * @param email Adresse email du membre
     * @param telephone Numéro de téléphone du membre
     * @param adresse Adresse postale du membre
     * @param dateInscription Date d'inscription du membre
     */
    public Membre(String nom, String prenom, String email, String telephone, String adresse, String dateInscription) {
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.email.set(email);
        this.telephone.set(telephone);
        this.adresse.set(adresse);
        this.dateInscription.set(dateInscription);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getPrenom() {
        return prenom.get();
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getTelephone() {
        return telephone.get();
    }

    public void setTelephone(String telephone) {
        this.telephone.set(telephone);
    }

    public StringProperty telephoneProperty() {
        return telephone;
    }

    public String getAdresse() {
        return adresse.get();
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public StringProperty adresseProperty() {
        return adresse;
    }

    public String getDateInscription() {
        return dateInscription.get();
    }

    public void setDateInscription(String dateInscription) {
        this.dateInscription.set(dateInscription);
    }

    public StringProperty dateInscriptionProperty() {
        return dateInscription;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
    
    /**
     * Retourne le nom complet du membre (prénom + nom).
     * 
     * @return Le nom complet du membre
     */
    public String getNomComplet() {
        return getPrenom() + " " + getNom();
    }
}
