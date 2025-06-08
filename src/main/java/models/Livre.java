package models;

import javafx.beans.property.*;

/**
 * Classe représentant un livre dans le système de gestion de bibliothèque.
 */
public class Livre {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titre = new SimpleStringProperty();
    private final StringProperty auteur = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final IntegerProperty anneePublication = new SimpleIntegerProperty();
    private final StringProperty editeur = new SimpleStringProperty();
    private final BooleanProperty disponible = new SimpleBooleanProperty(true);

    /**
     * Constructeur par défaut.
     */
    public Livre() {
    }

    /**
     * Constructeur avec paramètres.
     * 
     * @param id Identifiant unique du livre
     * @param titre Titre du livre
     * @param auteur Auteur du livre
     * @param isbn Numéro ISBN du livre
     * @param anneePublication Année de publication
     * @param editeur Maison d'édition
     * @param disponible Disponibilité du livre
     */
    public Livre(int id, String titre, String auteur, String isbn, int anneePublication, String editeur, boolean disponible) {
        this.id.set(id);
        this.titre.set(titre);
        this.auteur.set(auteur);
        this.isbn.set(isbn);
        this.anneePublication.set(anneePublication);
        this.editeur.set(editeur);
        this.disponible.set(disponible);
    }

    /**
     * Constructeur sans ID pour la création de nouveaux livres.
     * 
     * @param titre Titre du livre
     * @param auteur Auteur du livre
     * @param isbn Numéro ISBN du livre
     * @param anneePublication Année de publication
     * @param editeur Maison d'édition
     * @param disponible Disponibilité du livre
     */
    public Livre(String titre, String auteur, String isbn, int anneePublication, String editeur, boolean disponible) {
        this.titre.set(titre);
        this.auteur.set(auteur);
        this.isbn.set(isbn);
        this.anneePublication.set(anneePublication);
        this.editeur.set(editeur);
        this.disponible.set(disponible);
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

    public String getTitre() {
        return titre.get();
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public String getAuteur() {
        return auteur.get();
    }

    public void setAuteur(String auteur) {
        this.auteur.set(auteur);
    }

    public StringProperty auteurProperty() {
        return auteur;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public void setIsbn(String isbn) {
        this.isbn.set(isbn);
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public int getAnneePublication() {
        return anneePublication.get();
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication.set(anneePublication);
    }

    public IntegerProperty anneePublicationProperty() {
        return anneePublication;
    }

    public String getEditeur() {
        return editeur.get();
    }

    public void setEditeur(String editeur) {
        this.editeur.set(editeur);
    }

    public StringProperty editeurProperty() {
        return editeur;
    }

    public boolean isDisponible() {
        return disponible.get();
    }

    public void setDisponible(boolean disponible) {
        this.disponible.set(disponible);
    }

    public BooleanProperty disponibleProperty() {
        return disponible;
    }

    @Override
    public String toString() {
        return getTitre() + " (" + getAuteur() + ")";
    }
}
