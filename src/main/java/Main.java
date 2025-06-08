import controllers.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DatabaseConnection;

import java.io.IOException;

/**
 * Classe principale de l'application de gestion de bibliothèque.
 * Point d'entrée de l'application JavaFX.
 */
public class Main extends Application {

    /**
     * Méthode principale qui lance l'application.
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        
        launch(args);
    }

    /**
     * Méthode appelée lors du démarrage de l'application JavaFX.
     * Configure la fenêtre principale et affiche l'interface utilisateur.
     * 
     * @param primaryStage La fenêtre principale de l'application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            MainController mainController = new MainController();
            
            Scene scene = mainController.getScene();
            
            primaryStage.setTitle("Gestion de Bibliothèque");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des fichiers FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode appelée lors de la fermeture de l'application.
     * Ferme toutes les connexions à la base de données.
     */
    @Override
    public void stop() {
        DatabaseConnection.closeAllConnections();
    }
}
