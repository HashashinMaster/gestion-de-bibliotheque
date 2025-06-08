package dao.impl;

import dao.MembreDAO;
import models.Membre;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface MembreDAO pour les opérations CRUD sur les membres.
 */
public class MembreDAOImpl implements MembreDAO {

    /**
     * Insère un nouveau membre dans la base de données.
     * 
     * @param membre Le membre à insérer
     * @return Le membre inséré avec son ID généré
     * @throws Exception En cas d'erreur lors de l'insertion
     */
    @Override
    public Membre insert(Membre membre) throws Exception {
        String sql = "INSERT INTO membres (nom, prenom, email, telephone, adresse, date_inscription) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getEmail());
            pstmt.setString(4, membre.getTelephone());
            pstmt.setString(5, membre.getAdresse());
            pstmt.setString(6, membre.getDateInscription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création du membre a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    membre.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du membre a échoué, aucun ID obtenu.");
                }
            }
            
            return membre;
        }
    }

    /**
     * Met à jour un membre existant dans la base de données.
     * 
     * @param membre Le membre à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    @Override
    public boolean update(Membre membre) throws Exception {
        String sql = "UPDATE membres SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ?, date_inscription = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getEmail());
            pstmt.setString(4, membre.getTelephone());
            pstmt.setString(5, membre.getAdresse());
            pstmt.setString(6, membre.getDateInscription());
            pstmt.setInt(7, membre.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprime un membre de la base de données par son ID.
     * 
     * @param id L'ID du membre à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la suppression
     */
    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM membres WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Récupère un membre par son ID.
     * 
     * @param id L'ID du membre à récupérer
     * @return Le membre correspondant à l'ID, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public Membre findById(int id) throws Exception {
        String sql = "SELECT * FROM membres WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMembreFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Récupère tous les membres.
     * 
     * @return Une liste de tous les membres
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public List<Membre> findAll() throws Exception {
        String sql = "SELECT * FROM membres";
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(extractMembreFromResultSet(rs));
            }
        }
        
        return membres;
    }

    /**
     * Recherche des membres par nom.
     * 
     * @param nom Le nom à rechercher
     * @return Liste des membres correspondant au nom
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Membre> findByNom(String nom) throws Exception {
        String sql = "SELECT * FROM membres WHERE nom LIKE ?";
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(extractMembreFromResultSet(rs));
                }
            }
        }
        
        return membres;
    }

    /**
     * Recherche un membre par email.
     * 
     * @param email L'email à rechercher
     * @return Le membre correspondant à l'email, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public Membre findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM membres WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMembreFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Recherche des membres par nom complet (nom et prénom).
     * 
     * @param nom Le nom à rechercher
     * @param prenom Le prénom à rechercher
     * @return Liste des membres correspondant au nom complet
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Membre> findByNomComplet(String nom, String prenom) throws Exception {
        String sql = "SELECT * FROM membres WHERE nom LIKE ? AND prenom LIKE ?";
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            pstmt.setString(2, "%" + prenom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(extractMembreFromResultSet(rs));
                }
            }
        }
        
        return membres;
    }
    
    /**
     * Extrait un objet Membre d'un ResultSet.
     * 
     * @param rs Le ResultSet contenant les données du membre
     * @return Un objet Membre
     * @throws SQLException En cas d'erreur lors de l'extraction
     */
    private Membre extractMembreFromResultSet(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setId(rs.getInt("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setEmail(rs.getString("email"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setAdresse(rs.getString("adresse"));
        membre.setDateInscription(rs.getString("date_inscription"));
        return membre;
    }
}
