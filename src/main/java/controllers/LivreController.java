package controllers;

import dao.LivreDAO;
import dao.impl.LivreDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Livre;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import utils.EventSystem;

/**
 * Contrôleur pour la gestion des livres.
 */
public class LivreController implements Initializable {
    
    private final LivreDAO livreDAO;
    private final ObservableList<Livre> livresList;
    
    @FXML
    private TableView<Livre> livresTable;
    
    @FXML
    private TableColumn<Livre, Integer> idColumn;
    
    @FXML
    private TableColumn<Livre, String> titreColumn;
    
    @FXML
    private TableColumn<Livre, String> auteurColumn;
    
    @FXML
    private TableColumn<Livre, String> isbnColumn;
    
    @FXML
    private TableColumn<Livre, Integer> anneePublicationColumn;
    
    @FXML
    private TableColumn<Livre, String> editeurColumn;
    
    @FXML
    private TableColumn<Livre, Boolean> disponibleColumn;
    
    @FXML
    private TextField titreField;
    
    @FXML
    private TextField auteurField;
    
    @FXML
    private TextField isbnField;
    
    @FXML
    private TextField anneePublicationField;
    
    @FXML
    private TextField editeurField;
    
    @FXML
    private CheckBox disponibleCheck;
    
    @FXML
    private TextField searchField;
    
    /**
     * Constructeur du contrôleur de livres.
     * Initialise le DAO et la liste observable des livres.
     */
    public LivreController() {
        this.livreDAO = new LivreDAOImpl();
        this.livresList = FXCollections.observableArrayList();
    }
    
    /**
     * Méthode appelée automatiquement après le chargement du fichier FXML.
     * Initialise les composants et charge les données.
     * 
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        titreColumn.setCellValueFactory(cellData -> cellData.getValue().titreProperty());
        auteurColumn.setCellValueFactory(cellData -> cellData.getValue().auteurProperty());
        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbnProperty());
        anneePublicationColumn.setCellValueFactory(cellData -> cellData.getValue().anneePublicationProperty().asObject());
        editeurColumn.setCellValueFactory(cellData -> cellData.getValue().editeurProperty());
        disponibleColumn.setCellValueFactory(cellData -> cellData.getValue().disponibleProperty());
        
        disponibleColumn.setCellFactory(column -> new TableCell<Livre, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Oui" : "Non");
                }
            }
        });
        
        livresTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            }
        });
        
        livresTable.setItems(livresList);
        
        loadLivres();
        
        EventSystem.getInstance().subscribe("LIVRE_MODIFIED", data -> refreshData());
    }
    
    /**
     * Charge tous les livres depuis la base de données.
     */
    private void loadLivres() {
        try {
            livresList.clear();
            livresList.addAll(livreDAO.findAll());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des livres", e.getMessage());
        }
    }
    
    /**
     * Rafraîchit toutes les données affichées dans la vue.
     */
    public void refreshData() {
        loadLivres();
    }
    
    /**
     * Recherche des livres par titre ou auteur.
     * Méthode appelée par le bouton de recherche dans le FXML.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        try {
            if (query == null || query.trim().isEmpty()) {
                loadLivres();
            } else {
                livresList.clear();
                livresList.addAll(livreDAO.findByTitre(query));
                livresList.addAll(livreDAO.findByAuteur(query));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche des livres", e.getMessage());
        }
    }
    
    /**
     * Méthode appelée par le bouton de réinitialisation dans le FXML.
     */
    @FXML
    private void handleReset() {
        searchField.clear();
        loadLivres();
    }
    
    /**
     * Remplit le formulaire avec les données d'un livre.
     * 
     * @param livre Le livre à afficher dans le formulaire
     */
    private void fillForm(Livre livre) {
        titreField.setText(livre.getTitre());
        auteurField.setText(livre.getAuteur());
        isbnField.setText(livre.getIsbn());
        anneePublicationField.setText(String.valueOf(livre.getAnneePublication()));
        editeurField.setText(livre.getEditeur());
        disponibleCheck.setSelected(livre.isDisponible());
    }
    
    /**
     * Efface le formulaire.
     * Méthode appelée par le bouton d'effacement dans le FXML.
     */
    @FXML
    private void clearForm() {
        titreField.clear();
        auteurField.clear();
        isbnField.clear();
        anneePublicationField.clear();
        editeurField.clear();
        disponibleCheck.setSelected(true);
        livresTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Ajoute un nouveau livre.
     * Méthode appelée par le bouton d'ajout dans le FXML.
     */
    @FXML
    private void addLivre() {
        try {
            if (titreField.getText().isEmpty() || auteurField.getText().isEmpty() || isbnField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Champs obligatoires", 
                          "Les champs Titre, Auteur et ISBN sont obligatoires.");
                return;
            }
            
            int anneePublication;
            try {
                anneePublication = Integer.parseInt(anneePublicationField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Année invalide", 
                          "L'année de publication doit être un nombre entier.");
                return;
            }
            
            Livre livre = new Livre(
                titreField.getText(),
                auteurField.getText(),
                isbnField.getText(),
                anneePublication,
                editeurField.getText(),
                disponibleCheck.isSelected()
            );
            
            Livre addedLivre = livreDAO.insert(livre);
            
            livresList.add(addedLivre);
            
            clearForm();
            
            EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Livre ajouté", 
                      "Le livre a été ajouté avec succès.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du livre", e.getMessage());
        }
    }
    
    /**
     * Met à jour un livre existant.
     * Méthode appelée par le bouton de modification dans le FXML.
     */
    @FXML
    private void updateLivre() {
        Livre selectedLivre = livresTable.getSelectionModel().getSelectedItem();
        
        if (selectedLivre == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun livre sélectionné", 
                      "Veuillez sélectionner un livre à modifier.");
            return;
        }
        
        try {
            if (titreField.getText().isEmpty() || auteurField.getText().isEmpty() || isbnField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Champs obligatoires", 
                          "Les champs Titre, Auteur et ISBN sont obligatoires.");
                return;
            }
            
            int anneePublication;
            try {
                anneePublication = Integer.parseInt(anneePublicationField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Année invalide", 
                          "L'année de publication doit être un nombre entier.");
                return;
            }
            
            selectedLivre.setTitre(titreField.getText());
            selectedLivre.setAuteur(auteurField.getText());
            selectedLivre.setIsbn(isbnField.getText());
            selectedLivre.setAnneePublication(anneePublication);
            selectedLivre.setEditeur(editeurField.getText());
            selectedLivre.setDisponible(disponibleCheck.isSelected());
            
            boolean success = livreDAO.update(selectedLivre);
            
            if (success) {
                livresTable.refresh();
                
                EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Livre modifié", 
                          "Le livre a été modifié avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification", 
                          "La modification du livre a échoué.");
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du livre", e.getMessage());
        }
    }
    
    /**
     * Supprime un livre existant.
     * Méthode appelée par le bouton de suppression dans le FXML.
     */
    @FXML
    private void deleteLivre() {
        Livre selectedLivre = livresTable.getSelectionModel().getSelectedItem();
        
        if (selectedLivre == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun livre sélectionné", 
                      "Veuillez sélectionner un livre à supprimer.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le livre");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce livre ?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = livreDAO.delete(selectedLivre.getId());
                
                if (success) {
                    livresList.remove(selectedLivre);
                    
                    clearForm();
                    
                    EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livre supprimé", 
                              "Le livre a été supprimé avec succès.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", 
                              "La suppression du livre a échoué.");
                }
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du livre", e.getMessage());
            }
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte.
     * 
     * @param type Le type d'alerte
     * @param title Le titre de l'alerte
     * @param header L'en-tête de l'alerte
     * @param content Le contenu de l'alerte
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Retourne la liste observable des livres.
     * 
     * @return La liste observable des livres
     */
    public ObservableList<Livre> getLivresList() {
        return livresList;
    }
    
    /**
     * Retourne le DAO des livres.
     * 
     * @return Le DAO des livres
     */
    public LivreDAO getLivreDAO() {
        return livreDAO;
    }
}
