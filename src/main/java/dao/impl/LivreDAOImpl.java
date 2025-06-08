package dao.impl;

import dao.LivreDAO;
import models.Livre;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface LivreDAO pour les opérations CRUD sur les livres.
 */
public class LivreDAOImpl implements LivreDAO {

    /**
     * Insère un nouveau livre dans la base de données.
     * 
     * @param livre Le livre à insérer
     * @return Le livre inséré avec son ID généré
     * @throws Exception En cas d'erreur lors de l'insertion
     */
    @Override
    public Livre insert(Livre livre) throws Exception {
        String sql = "INSERT INTO livres (titre, auteur, isbn, annee_publication, editeur, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getIsbn());
            pstmt.setInt(4, livre.getAnneePublication());
            pstmt.setString(5, livre.getEditeur());
            pstmt.setBoolean(6, livre.isDisponible());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création du livre a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    livre.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du livre a échoué, aucun ID obtenu.");
                }
            }
            
            return livre;
        }
    }

    /**
     * Met à jour un livre existant dans la base de données.
     * 
     * @param livre Le livre à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    @Override
    public boolean update(Livre livre) throws Exception {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, isbn = ?, annee_publication = ?, editeur = ?, disponible = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getIsbn());
            pstmt.setInt(4, livre.getAnneePublication());
            pstmt.setString(5, livre.getEditeur());
            pstmt.setBoolean(6, livre.isDisponible());
            pstmt.setInt(7, livre.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprime un livre de la base de données par son ID.
     * 
     * @param id L'ID du livre à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la suppression
     */
    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM livres WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Récupère un livre par son ID.
     * 
     * @param id L'ID du livre à récupérer
     * @return Le livre correspondant à l'ID, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public Livre findById(int id) throws Exception {
        String sql = "SELECT * FROM livres WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractLivreFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Récupère tous les livres.
     * 
     * @return Une liste de tous les livres
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public List<Livre> findAll() throws Exception {
        String sql = "SELECT * FROM livres";
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
        }
        
        return livres;
    }

    /**
     * Recherche des livres par titre.
     * 
     * @param titre Le titre à rechercher
     * @return Liste des livres correspondant au titre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Livre> findByTitre(String titre) throws Exception {
        String sql = "SELECT * FROM livres WHERE titre LIKE ?";
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + titre + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livres.add(extractLivreFromResultSet(rs));
                }
            }
        }
        
        return livres;
    }

    /**
     * Recherche un livre par ISBN.
     * 
     * @param isbn L'ISBN à rechercher
     * @return Le livre correspondant à l'ISBN, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public Livre findByISBN(String isbn) throws Exception {
        String sql = "SELECT * FROM livres WHERE isbn = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractLivreFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Recherche des livres par auteur.
     * 
     * @param auteur L'auteur à rechercher
     * @return Liste des livres correspondant à l'auteur
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Livre> findByAuteur(String auteur) throws Exception {
        String sql = "SELECT * FROM livres WHERE auteur LIKE ?";
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + auteur + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livres.add(extractLivreFromResultSet(rs));
                }
            }
        }
        
        return livres;
    }

    /**
     * Récupère tous les livres disponibles.
     * 
     * @return Liste des livres disponibles
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Livre> findAllAvailable() throws Exception {
        String sql = "SELECT * FROM livres WHERE disponible = 1";
        List<Livre> livres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
        }
        
        return livres;
    }

    /**
     * Met à jour la disponibilité d'un livre.
     * 
     * @param id L'ID du livre
     * @param disponible La nouvelle valeur de disponibilité
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    @Override
    public boolean updateDisponibilite(int id, boolean disponible) throws Exception {
        String sql = "UPDATE livres SET disponible = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, disponible);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Extrait un objet Livre d'un ResultSet.
     * 
     * @param rs Le ResultSet contenant les données du livre
     * @return Un objet Livre
     * @throws SQLException En cas d'erreur lors de l'extraction
     */
    private Livre extractLivreFromResultSet(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getInt("id"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setAnneePublication(rs.getInt("annee_publication"));
        livre.setEditeur(rs.getString("editeur"));
        livre.setDisponible(rs.getBoolean("disponible"));
        return livre;
    }
}
