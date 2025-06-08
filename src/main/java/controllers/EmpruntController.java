package controllers;

import dao.EmpruntDAO;
import dao.LivreDAO;
import dao.MembreDAO;
import dao.impl.EmpruntDAOImpl;
import dao.impl.LivreDAOImpl;
import dao.impl.MembreDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Emprunt;
import models.Livre;
import models.Membre;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import utils.EventSystem;

/**
 * Contrôleur pour la gestion des emprunts.
 */
public class EmpruntController implements Initializable {

    private final EmpruntDAO empruntDAO;
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;
    private final ObservableList<Emprunt> empruntsList;

    @FXML
    private TableView<Emprunt> empruntsTable;

    @FXML
    private TableColumn<Emprunt, Integer> idColumn;

    @FXML
    private TableColumn<Emprunt, String> livreColumn;

    @FXML
    private TableColumn<Emprunt, String> membreColumn;

    @FXML
    private TableColumn<Emprunt, String> dateEmpruntColumn;

    @FXML
    private TableColumn<Emprunt, String> dateRetourPrevueColumn;

    @FXML
    private TableColumn<Emprunt, String> dateRetourReelleColumn;

    @FXML
    private ComboBox<Livre> livreComboBox;

    @FXML
    private ComboBox<Membre> membreComboBox;

    @FXML
    private DatePicker dateEmpruntPicker;

    @FXML
    private DatePicker dateRetourPrevuePicker;

    @FXML
    private DatePicker dateRetourReellePicker;
    
    @FXML
    private TextField searchField;

    /**
     * Constructeur du contrôleur d'emprunts.
     * Initialise les DAOs et la liste observable des emprunts.
     */
    public EmpruntController() {
        this.livreDAO = new LivreDAOImpl();
        this.membreDAO = new MembreDAOImpl();
        this.empruntDAO = new EmpruntDAOImpl(livreDAO, membreDAO);
        this.empruntsList = FXCollections.observableArrayList();
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
        livreColumn.setCellValueFactory(cellData -> {
            Emprunt emprunt = cellData.getValue();
            if (emprunt != null) {
                Livre livre = emprunt.getLivre();
                if (livre != null) {
                    return livre.titreProperty();
                }
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        membreColumn.setCellValueFactory(cellData -> {
            Emprunt emprunt = cellData.getValue();
            if (emprunt != null) {
                Membre membre = emprunt.getMembre();
                if (membre != null) {
                    return new javafx.beans.property.SimpleStringProperty(membre.getNom() + " " + membre.getPrenom());
                }
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        dateEmpruntColumn.setCellValueFactory(cellData -> cellData.getValue().dateEmpruntProperty());
        dateRetourPrevueColumn.setCellValueFactory(cellData -> cellData.getValue().dateRetourPrevueProperty());
        dateRetourReelleColumn.setCellValueFactory(cellData -> cellData.getValue().dateRetourReelleProperty());
        
        empruntsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            }
        });
        
        empruntsTable.setItems(empruntsList);
        
        dateEmpruntPicker.setValue(LocalDate.now());
        dateRetourPrevuePicker.setValue(LocalDate.now().plusDays(14));
        
        refreshData();
        
        EventSystem.getInstance().subscribe("LIVRE_MODIFIED", data -> refreshData());
        EventSystem.getInstance().subscribe("MEMBRE_MODIFIED", data -> refreshData());
        EventSystem.getInstance().subscribe("EMPRUNT_VIEW_ACTIVATED", data -> refreshData());
    }
    
    /**
     * Rafraîchit toutes les données affichées dans la vue.
     */
    public void refreshData() {
        loadEmprunts();
        loadLivres();
        loadMembres();
    }

    /**
     * Charge tous les emprunts depuis la base de données.
     */
    private void loadEmprunts() {
        try {
            empruntsList.clear();
            List<Emprunt> emprunts = empruntDAO.findAllWithDetails();
            
            for (Emprunt emprunt : emprunts) {
                if (emprunt.getLivre() == null) {
                    Livre livre = livreDAO.findById(emprunt.getLivreId());
                    emprunt.setLivre(livre);
                }
                if (emprunt.getMembre() == null) {
                    Membre membre = membreDAO.findById(emprunt.getMembreId());
                    emprunt.setMembre(membre);
                }
            }
            
            empruntsList.addAll(emprunts);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des emprunts", e.getMessage());
        }
    }

    /**
     * Charge tous les livres depuis la base de données.
     */
    private void loadLivres() {
        try {
            livreComboBox.setItems(FXCollections.observableArrayList(livreDAO.findAll()));
            livreComboBox.setCellFactory(lv -> new ListCell<Livre>() {
                @Override
                protected void updateItem(Livre livre, boolean empty) {
                    super.updateItem(livre, empty);
                    if (empty || livre == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String disponibiliteText = livre.isDisponible() ? "[Disponible]" : "[Indisponible]"; 
                        setText(livre.getTitre() + " (" + livre.getAuteur() + ") " + disponibiliteText);
                        
                        if (!livre.isDisponible()) {
                        } else {
                        }
                    }
                }
            });
            livreComboBox.setButtonCell(new ListCell<Livre>() {
                @Override
                protected void updateItem(Livre livre, boolean empty) {
                    super.updateItem(livre, empty);
                    if (empty || livre == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String disponibiliteText = livre.isDisponible() ? "[Disponible]" : "[Indisponible]"; 
                        setText(livre.getTitre() + " (" + livre.getAuteur() + ") " + disponibiliteText);
                        
                        if (!livre.isDisponible()) {
                        } else {
                        }
                    }
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des livres", e.getMessage());
        }
    }

    /**
     * Charge tous les membres depuis la base de données.
     */
    private void loadMembres() {
        try {
            membreComboBox.setItems(FXCollections.observableArrayList(membreDAO.findAll()));
            membreComboBox.setCellFactory(lv -> new ListCell<Membre>() {
                @Override
                protected void updateItem(Membre membre, boolean empty) {
                    super.updateItem(membre, empty);
                    setText(empty ? "" : membre.getNom() + " " + membre.getPrenom());
                }
            });
            membreComboBox.setButtonCell(new ListCell<Membre>() {
                @Override
                protected void updateItem(Membre membre, boolean empty) {
                    super.updateItem(membre, empty);
                    setText(empty ? "" : membre.getNom() + " " + membre.getPrenom());
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des membres", e.getMessage());
        }
    }

    /**
     * Remplit le formulaire avec les données d'un emprunt.
     *
     * @param emprunt L'emprunt à afficher dans le formulaire
     */
    private void fillForm(Emprunt emprunt) {
        livreComboBox.setValue(emprunt.getLivre());
        membreComboBox.setValue(emprunt.getMembre());

        try {
            LocalDate dateEmprunt = LocalDate.parse(emprunt.getDateEmprunt());
            dateEmpruntPicker.setValue(dateEmprunt);

            LocalDate dateRetourPrevue = LocalDate.parse(emprunt.getDateRetourPrevue());
            dateRetourPrevuePicker.setValue(dateRetourPrevue);

            if (emprunt.getDateRetourReelle() != null && !emprunt.getDateRetourReelle().isEmpty()) {
                LocalDate dateRetourReelle = LocalDate.parse(emprunt.getDateRetourReelle());
                dateRetourReellePicker.setValue(dateRetourReelle);
            }
        } catch (Exception e) {
            dateEmpruntPicker.setValue(LocalDate.now());
            dateRetourPrevuePicker.setValue(LocalDate.now().plusDays(14));
        }
    }

    /**
     * Efface le formulaire.
     * Méthode appelée par le bouton d'effacement dans le FXML.
     */
    @FXML
    private void clearForm() {
        livreComboBox.setValue(null);
        membreComboBox.setValue(null);
        dateEmpruntPicker.setValue(LocalDate.now());
        dateRetourPrevuePicker.setValue(LocalDate.now().plusDays(14));
        dateRetourReellePicker.setValue(null);
        empruntsTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Ajoute un nouvel emprunt.
     * Méthode appelée par le bouton d'ajout dans le FXML.
     */
    @FXML
    private void addEmprunt() {
        try {
            if (livreComboBox.getValue() == null || membreComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Sélections obligatoires",
                        "Veuillez sélectionner un livre et un membre.");
                return;
            }

            if (!livreComboBox.getValue().isDisponible()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Livre non disponible", 
                        "Le livre sélectionné n'est pas disponible pour l'emprunt.");
                return;
            }

            String dateEmprunt = dateEmpruntPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dateRetourPrevue = dateRetourPrevuePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);

            Emprunt emprunt = new Emprunt(
                    livreComboBox.getValue().getId(),
                    membreComboBox.getValue().getId(),
                    dateEmprunt,
                    dateRetourPrevue
            );

            emprunt.setLivre(livreComboBox.getValue());
            emprunt.setMembre(membreComboBox.getValue());

            Emprunt addedEmprunt = empruntDAO.insert(emprunt);

            empruntsList.add(addedEmprunt);

            livreComboBox.getValue().setDisponible(false);
            livreDAO.updateDisponibilite(livreComboBox.getValue().getId(), false);

            loadLivres();
            
            EventSystem.getInstance().publish("LIVRE_MODIFIED", null);

            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Emprunt enregistré",
                    "L'emprunt a été enregistré avec succès.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement de l'emprunt", e.getMessage());
        }
    }
    
    /**
     * Met à jour un emprunt existant.
     * Méthode appelée par le bouton de modification dans le FXML.
     */
    @FXML
    private void updateEmprunt() {
        Emprunt selectedEmprunt = empruntsTable.getSelectionModel().getSelectedItem();
        
        if (selectedEmprunt == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun emprunt sélectionné",
                    "Veuillez sélectionner un emprunt à modifier.");
            return;
        }
        
        try {
            if (livreComboBox.getValue() == null || membreComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Sélections obligatoires",
                        "Veuillez sélectionner un livre et un membre.");
                return;
            }
            
            String dateEmprunt = dateEmpruntPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dateRetourPrevue = dateRetourPrevuePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dateRetourReelle = null;
            
            if (dateRetourReellePicker.getValue() != null) {
                dateRetourReelle = dateRetourReellePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            selectedEmprunt.setLivreId(livreComboBox.getValue().getId());
            selectedEmprunt.setMembreId(membreComboBox.getValue().getId());
            selectedEmprunt.setDateEmprunt(dateEmprunt);
            selectedEmprunt.setDateRetourPrevue(dateRetourPrevue);
            selectedEmprunt.setDateRetourReelle(dateRetourReelle);
            selectedEmprunt.setLivre(livreComboBox.getValue());
            selectedEmprunt.setMembre(membreComboBox.getValue());
            
            empruntDAO.update(selectedEmprunt);
            
            loadEmprunts();
            
            EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
            
            clearForm();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Emprunt modifié",
                    "L'emprunt a été modifié avec succès.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de l'emprunt", e.getMessage());
        }
    }
    
    /**
     * Supprime un emprunt existant.
     * Méthode appelée par le bouton de suppression dans le FXML.
     */
    @FXML
    private void deleteEmprunt() {
        Emprunt selectedEmprunt = empruntsTable.getSelectionModel().getSelectedItem();
        
        if (selectedEmprunt == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun emprunt sélectionné",
                    "Veuillez sélectionner un emprunt à supprimer.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Confirmer la suppression");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cet emprunt ?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                empruntDAO.delete(selectedEmprunt.getId());
                
                if (selectedEmprunt.getDateRetourReelle() == null || selectedEmprunt.getDateRetourReelle().isEmpty()) {
                    Livre livre = selectedEmprunt.getLivre();
                    if (livre != null) {
                        livre.setDisponible(true);
                        livreDAO.updateDisponibilite(livre.getId(), true);
                    }
                }
                
                empruntsList.remove(selectedEmprunt);
                
                loadLivres();
                
                EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
                
                clearForm();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Emprunt supprimé",
                        "L'emprunt a été supprimé avec succès.");
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'emprunt", e.getMessage());
            }
        }
    }

    /**
     * Enregistre le retour d'un emprunt.
     */
    @FXML
    private void returnEmprunt() {
        Emprunt selectedEmprunt = empruntsTable.getSelectionModel().getSelectedItem();

        if (selectedEmprunt == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun emprunt sélectionné",
                    "Veuillez sélectionner un emprunt à retourner.");
            return;
        }
        
        try {
            if (selectedEmprunt.getDateRetourReelle() != null && !selectedEmprunt.getDateRetourReelle().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Emprunt déjà retourné",
                        "Cet emprunt a déjà été retourné.");
                return;
            }
            
            String dateRetourReelle;
            
            if (dateRetourReellePicker.getValue() != null) {
                dateRetourReelle = dateRetourReellePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                dateRetourReelle = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            selectedEmprunt.setDateRetourReelle(dateRetourReelle);
            
            empruntDAO.update(selectedEmprunt);
            
            Livre livre = selectedEmprunt.getLivre();
            livre.setDisponible(true);
            livreDAO.updateDisponibilite(livre.getId(), true);
            
            loadEmprunts();
            
            EventSystem.getInstance().publish("LIVRE_MODIFIED", null);
            
            clearForm();
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Retour enregistré",
                    "Le retour de l'emprunt a été enregistré avec succès.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement du retour", e.getMessage());
        }
    }
    
    /**
     * Recherche des emprunts par livre, membre ou date.
     * Méthode appelée par le bouton de recherche dans le FXML.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        try {
            if (query == null || query.isEmpty()) {
                return;
            }
            
            List<Emprunt> allEmprunts = empruntDAO.findAllWithDetails();
            List<Emprunt> filteredEmprunts = new ArrayList<>();
            
            for (Emprunt emprunt : allEmprunts) {
                if (emprunt.getLivre() != null && 
                    emprunt.getLivre().getTitre().toLowerCase().contains(query)) {
                    filteredEmprunts.add(emprunt);
                    continue;
                }
                
                if (emprunt.getMembre() != null && 
                    (emprunt.getMembre().getNom().toLowerCase().contains(query) || 
                     emprunt.getMembre().getPrenom().toLowerCase().contains(query))) {
                    filteredEmprunts.add(emprunt);
                    continue;
                }
                
                if (emprunt.getDateEmprunt().contains(query) || 
                    emprunt.getDateRetourPrevue().contains(query) || 
                    (emprunt.getDateRetourReelle() != null && emprunt.getDateRetourReelle().contains(query))) {
                    filteredEmprunts.add(emprunt);
                }
            }
            
            empruntsList.clear();
            empruntsList.addAll(filteredEmprunts);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }
    
    /**
     * Réinitialise la recherche et affiche tous les emprunts.
     * Méthode appelée par le bouton de réinitialisation dans le FXML.
     */
    @FXML
    private void handleReset() {
        searchField.clear();
        loadEmprunts();
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
}
