package models;

import javafx.beans.property.*;

/**
 * Classe représentant un emprunt de livre dans le système de gestion de bibliothèque.
 */
public class Emprunt {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty livreId = new SimpleIntegerProperty();
    private final IntegerProperty membreId = new SimpleIntegerProperty();
    private final StringProperty dateEmprunt = new SimpleStringProperty();
    private final StringProperty dateRetourPrevue = new SimpleStringProperty();
    private final StringProperty dateRetourReelle = new SimpleStringProperty();
    
    private final ObjectProperty<Livre> livre = new SimpleObjectProperty<>();
    private final ObjectProperty<Membre> membre = new SimpleObjectProperty<>();

    /**
     * Constructeur par défaut.
     */
    public Emprunt() {
    }

    /**
     * Constructeur avec paramètres.
     * 
     * @param id Identifiant unique de l'emprunt
     * @param livreId Identifiant du livre emprunté
     * @param membreId Identifiant du membre emprunteur
     * @param dateEmprunt Date de l'emprunt
     * @param dateRetourPrevue Date de retour prévue
     * @param dateRetourReelle Date de retour réelle (peut être null)
     */
    public Emprunt(int id, int livreId, int membreId, String dateEmprunt, String dateRetourPrevue, String dateRetourReelle) {
        this.id.set(id);
        this.livreId.set(livreId);
        this.membreId.set(membreId);
        this.dateEmprunt.set(dateEmprunt);
        this.dateRetourPrevue.set(dateRetourPrevue);
        this.dateRetourReelle.set(dateRetourReelle);
    }

    /**
     * Constructeur sans ID pour la création de nouveaux emprunts.
     * 
     * @param livreId Identifiant du livre emprunté
     * @param membreId Identifiant du membre emprunteur
     * @param dateEmprunt Date de l'emprunt
     * @param dateRetourPrevue Date de retour prévue
     */
    public Emprunt(int livreId, int membreId, String dateEmprunt, String dateRetourPrevue) {
        this.livreId.set(livreId);
        this.membreId.set(membreId);
        this.dateEmprunt.set(dateEmprunt);
        this.dateRetourPrevue.set(dateRetourPrevue);
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

    public int getLivreId() {
        return livreId.get();
    }

    public void setLivreId(int livreId) {
        this.livreId.set(livreId);
    }

    public IntegerProperty livreIdProperty() {
        return livreId;
    }

    public int getMembreId() {
        return membreId.get();
    }

    public void setMembreId(int membreId) {
        this.membreId.set(membreId);
    }

    public IntegerProperty membreIdProperty() {
        return membreId;
    }

    public String getDateEmprunt() {
        return dateEmprunt.get();
    }

    public void setDateEmprunt(String dateEmprunt) {
        this.dateEmprunt.set(dateEmprunt);
    }

    public StringProperty dateEmpruntProperty() {
        return dateEmprunt;
    }

    public String getDateRetourPrevue() {
        return dateRetourPrevue.get();
    }

    public void setDateRetourPrevue(String dateRetourPrevue) {
        this.dateRetourPrevue.set(dateRetourPrevue);
    }

    public StringProperty dateRetourPrevueProperty() {
        return dateRetourPrevue;
    }

    public String getDateRetourReelle() {
        return dateRetourReelle.get();
    }

    public void setDateRetourReelle(String dateRetourReelle) {
        this.dateRetourReelle.set(dateRetourReelle);
    }

    public StringProperty dateRetourReelleProperty() {
        return dateRetourReelle;
    }
    
    public Livre getLivre() {
        return livre.get();
    }
    
    public void setLivre(Livre livre) {
        this.livre.set(livre);
    }
    
    public ObjectProperty<Livre> livreProperty() {
        return livre;
    }
    
    public Membre getMembre() {
        return membre.get();
    }
    
    public void setMembre(Membre membre) {
        this.membre.set(membre);
    }
    
    public ObjectProperty<Membre> membreProperty() {
        return membre;
    }

    /**
     * Vérifie si l'emprunt est en cours (pas encore retourné).
     * 
     * @return true si l'emprunt est en cours, false sinon
     */
    public boolean isEnCours() {
        return dateRetourReelle.get() == null || dateRetourReelle.get().isEmpty();
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "id=" + getId() +
                ", livreId=" + getLivreId() +
                ", membreId=" + getMembreId() +
                ", dateEmprunt='" + getDateEmprunt() + '\'' +
                ", dateRetourPrevue='" + getDateRetourPrevue() + '\'' +
                ", dateRetourReelle='" + getDateRetourReelle() + '\'' +
                '}';
    }
}
