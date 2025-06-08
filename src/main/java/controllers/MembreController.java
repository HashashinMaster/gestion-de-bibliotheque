package controllers;

import dao.MembreDAO;
import dao.impl.MembreDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Membre;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import utils.EventSystem;

/**
 * Contrôleur pour la gestion des membres.
 */
public class MembreController implements Initializable {
    
    private final MembreDAO membreDAO;
    private final ObservableList<Membre> membresList;
    
    @FXML
    private TableView<Membre> membresTable;
    
    @FXML
    private TableColumn<Membre, Integer> idColumn;
    
    @FXML
    private TableColumn<Membre, String> nomColumn;
    
    @FXML
    private TableColumn<Membre, String> prenomColumn;
    
    @FXML
    private TableColumn<Membre, String> emailColumn;
    
    @FXML
    private TableColumn<Membre, String> telephoneColumn;
    
    @FXML
    private TableColumn<Membre, String> adresseColumn;
    
    @FXML
    private TableColumn<Membre, String> dateInscriptionColumn;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField adresseField;
    
    @FXML
    private DatePicker dateInscriptionPicker;
    
    @FXML
    private TextField searchField;
    
    /**
     * Constructeur du contrôleur de membres.
     * Initialise le DAO et la liste observable des membres.
     */
    public MembreController() {
        this.membreDAO = new MembreDAOImpl();
        this.membresList = FXCollections.observableArrayList();
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
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
        adresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
        dateInscriptionColumn.setCellValueFactory(cellData -> cellData.getValue().dateInscriptionProperty());
        
        membresTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            }
        });
        
        membresTable.setItems(membresList);
        
        loadMembres();
    }
    
    /**
     * Charge tous les membres depuis la base de données.
     */
    private void loadMembres() {
        try {
            membresList.clear();
            membresList.addAll(membreDAO.findAll());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des membres", e.getMessage());
        }
    }
    
    /**
     * Recherche des membres par nom ou prénom.
     * Méthode appelée par le bouton de recherche dans le FXML.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        try {
            if (query == null || query.trim().isEmpty()) {
                loadMembres();
            } else {
                membresList.clear();
                membresList.addAll(membreDAO.findByNom(query));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche des membres", e.getMessage());
        }
    }
    
    /**
     * Méthode appelée par le bouton de réinitialisation dans le FXML.
     */
    @FXML
    private void handleReset() {
        searchField.clear();
        loadMembres();
    }
    

    

    
    /**
     * Remplit le formulaire avec les données d'un membre.
     * 
     * @param membre Le membre à afficher dans le formulaire
     */
    private void fillForm(Membre membre) {
        nomField.setText(membre.getNom());
        prenomField.setText(membre.getPrenom());
        emailField.setText(membre.getEmail());
        telephoneField.setText(membre.getTelephone());
        adresseField.setText(membre.getAdresse());
        
        try {
            LocalDate date = LocalDate.parse(membre.getDateInscription());
            dateInscriptionPicker.setValue(date);
        } catch (Exception e) {
            dateInscriptionPicker.setValue(LocalDate.now());
        }
    }
    
    /**
     * Efface le formulaire.
     * Méthode appelée par le bouton d'effacement dans le FXML.
     */
    @FXML
    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();
        dateInscriptionPicker.setValue(LocalDate.now());
        membresTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Ajoute un nouveau membre.
     * Méthode appelée par le bouton d'ajout dans le FXML.
     */
    @FXML
    private void addMembre() {
        try {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Champs obligatoires", 
                          "Les champs Nom, Prénom et Email sont obligatoires.");
                return;
            }
            
            String dateInscription = dateInscriptionPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            Membre membre = new Membre(
                nomField.getText(),
                prenomField.getText(),
                emailField.getText(),
                telephoneField.getText(),
                adresseField.getText(),
                dateInscription
            );
            
            Membre addedMembre = membreDAO.insert(membre);
            
            membresList.add(addedMembre);
            
            clearForm();
            
            EventSystem.getInstance().publish("MEMBRE_MODIFIED", null);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre ajouté", 
                      "Le membre a été ajouté avec succès.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du membre", e.getMessage());
        }
    }
    
    /**
     * Met à jour un membre existant.
     * Méthode appelée par le bouton de modification dans le FXML.
     */
    @FXML
    private void updateMembre() {
        Membre selectedMembre = membresTable.getSelectionModel().getSelectedItem();
        
        if (selectedMembre == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun membre sélectionné", 
                      "Veuillez sélectionner un membre à modifier.");
            return;
        }
        
        try {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Champs obligatoires", 
                          "Les champs Nom, Prénom et Email sont obligatoires.");
                return;
            }
            
            String dateInscription = dateInscriptionPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            selectedMembre.setNom(nomField.getText());
            selectedMembre.setPrenom(prenomField.getText());
            selectedMembre.setEmail(emailField.getText());
            selectedMembre.setTelephone(telephoneField.getText());
            selectedMembre.setAdresse(adresseField.getText());
            selectedMembre.setDateInscription(dateInscription);
            
            boolean success = membreDAO.update(selectedMembre);
            
            if (success) {
                membresTable.refresh();
                
                EventSystem.getInstance().publish("MEMBRE_MODIFIED", null);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre modifié", 
                          "Le membre a été modifié avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification", 
                          "La modification du membre a échoué.");
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du membre", e.getMessage());
        }
    }
    
    /**
     * Supprime un membre existant.
     * Méthode appelée par le bouton de suppression dans le FXML.
     */
    @FXML
    private void deleteMembre() {
        Membre selectedMembre = membresTable.getSelectionModel().getSelectedItem();
        
        if (selectedMembre == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun membre sélectionné", 
                      "Veuillez sélectionner un membre à supprimer.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le membre");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce membre ?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = membreDAO.delete(selectedMembre.getId());
                
                if (success) {
                    membresList.remove(selectedMembre);
                    
                    clearForm();
                    
                    EventSystem.getInstance().publish("MEMBRE_MODIFIED", null);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre supprimé", 
                              "Le membre a été supprimé avec succès.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", 
                              "La suppression du membre a échoué.");
                }
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du membre", e.getMessage());
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
     * Retourne la liste observable des membres.
     * 
     * @return La liste observable des membres
     */
    public ObservableList<Membre> getMembresList() {
        return membresList;
    }
    
    /**
     * Retourne le DAO des membres.
     * 
     * @return Le DAO des membres
     */
    public MembreDAO getMembreDAO() {
        return membreDAO;
    }
}
