package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import utils.EventSystem;

import java.io.IOException;

/**
 * Contrôleur principal de l'application.
 * Gère la navigation entre les différentes vues.
 */
public class MainController {
    
    @FXML
    private TabPane tabPane;
    
    private BorderPane mainLayout;
    
    /**
     * Constructeur par défaut requis pour l'initialisation FXML.
     */
    public MainController() {
        // Constructeur vide requis pour FXML
    }
    
    /**
     * Initialise le contrôleur après que le fichier FXML a été chargé.
     * Cette méthode est automatiquement appelée par le chargeur FXML.
     */
    @FXML
    private void initialize() {
       
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null && "Emprunts".equals(newTab.getText())) {
                // Publier un événement pour informer EmpruntController que sa vue est activée
                EventSystem.getInstance().publish("EMPRUNT_VIEW_ACTIVATED", null);
            }
        });
    }
    
    /**
     * Charge la vue principale depuis le fichier FXML.
     * 
     * @return La scène principale de l'application
     * @throws IOException Si le chargement du fichier FXML échoue
     */
    public Scene getScene() throws IOException {
        if (mainLayout == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            mainLayout = loader.load();
        }
        return new Scene(mainLayout, 1000, 700);
    }
    

}
