package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Classe utilitaire pour gérer la connexion à la base de données MySQL.
 * Implémente un pool de connexions basique pour éviter les problèmes de connexions fermées.
 */
public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))
            .filename(".env")
            .ignoreIfMissing()
            .load();
    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "3307");
    private static final String DB_NAME = dotenv.get("DB_NAME", "bibliotheque");
    private static final String DB_USER = dotenv.get("DB_USER", "root");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "");
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";
    
    private static final int MAX_CONNECTIONS = 10;
    private static Connection[] connectionPool = new Connection[MAX_CONNECTIONS];
    private static boolean[] connectionInUse = new boolean[MAX_CONNECTIONS];
    private static boolean poolInitialized = false;
    
    /**
     * Initialise le pool de connexions.
     */
    private static void initializePool() {

        if (!poolInitialized) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                for (int i = 0; i < MAX_CONNECTIONS; i++) {
                    connectionPool[i] = null;
                    connectionInUse[i] = false;
                }
                
                poolInitialized = true;
                System.out.println("Pool de connexions initialisé.");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL non trouvé: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Établit une connexion à la base de données depuis le pool.
     * @return Une instance de Connection
     */
    public static synchronized Connection getConnection() {
        if (!poolInitialized) {
            initializePool();
        }
        
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (!connectionInUse[i]) {
                try {
                    if (connectionPool[i] == null || connectionPool[i].isClosed()) {
                        connectionPool[i] = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        System.out.println("Nouvelle connexion créée dans le pool (index " + i + ").");
                    }
                    
                    connectionInUse[i] = true;
                    return connectionPool[i];
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la création d'une connexion: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        try {
            System.out.println("Pool de connexions saturé, création d'une connexion temporaire.");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création d'une connexion temporaire: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Libère une connexion du pool.
     * @param connection La connexion à libérer
     */
    public static synchronized void releaseConnection(Connection connection) {
        if (connection == null) return;
        
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (connection == connectionPool[i]) {
                connectionInUse[i] = false;
                System.out.println("Connexion libérée dans le pool (index " + i + ").");
                return;
            }
        }
        
        try {
            connection.close();
            System.out.println("Connexion temporaire fermée.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture d'une connexion temporaire: " + e.getMessage());
        }
    }
    
    /**
     * Ferme toutes les connexions du pool.
     */
    public static synchronized void closeAllConnections() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (connectionPool[i] != null) {
                try {
                    connectionPool[i].close();
                    connectionPool[i] = null;
                    connectionInUse[i] = false;
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture d'une connexion du pool: " + e.getMessage());
                }
            }
        }
        System.out.println("Toutes les connexions du pool ont été fermées.");
    }
    
    /**
     * Initialise la base de données en exécutant le script SQL.
     */
    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            
            String sqlScript = loadSqlScript();
            
            String[] queries = sqlScript.split(";");
            
            for (String query : queries) {
                query = query.trim();
                if (!query.isEmpty() && query.toUpperCase().startsWith("CREATE TABLE")) {
                    try {
                        stmt.execute(query);
                    } catch (SQLException e) {
                        System.out.println("Info: " + e.getMessage());
                    }
                }
            }
            
            boolean hasData = false;
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery("SELECT COUNT(*) FROM livres");
                if (rs.next() && rs.getInt(1) > 0) {
                    hasData = true;
                }
            } catch (SQLException e) {
                System.out.println("Info: Vérification des données - " + e.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        System.err.println("Erreur lors de la fermeture du ResultSet: " + e.getMessage());
                    }
                }
            }
            
            if (!hasData) {
                for (String query : queries) {
                    query = query.trim();
                    if (!query.isEmpty() && query.toUpperCase().startsWith("INSERT")) {
                        try {
                            stmt.execute(query);
                        } catch (SQLException e) {
                            System.err.println("Erreur lors de l'insertion des données: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Données d'exemple insérées avec succès.");
            } else {
                System.out.println("Des données existent déjà, aucune insertion nécessaire.");
            }
            
            System.out.println("Base de données MySQL initialisée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données MySQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture du Statement: " + e.getMessage());
                }
            }
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }
    
    /**
     * Charge le script SQL depuis les ressources.
     * @return Le contenu du script SQL
     */
    private static String loadSqlScript() {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.sql")) {
            if (is == null) {
                throw new IOException("Le fichier database.sql n'a pas été trouvé dans les ressources.");
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du script SQL: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
