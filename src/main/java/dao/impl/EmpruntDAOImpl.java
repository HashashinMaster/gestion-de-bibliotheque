package dao.impl;

import dao.EmpruntDAO;
import dao.LivreDAO;
import dao.MembreDAO;
import models.Emprunt;
import models.Livre;
import models.Membre;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface EmpruntDAO pour les opérations CRUD sur les emprunts.
 */
public class EmpruntDAOImpl implements EmpruntDAO {
    
    private final LivreDAO livreDAO;
    private final MembreDAO membreDAO;
    
    /**
     * Constructeur avec injection des dépendances.
     * 
     * @param livreDAO DAO pour les opérations sur les livres
     * @param membreDAO DAO pour les opérations sur les membres
     */
    public EmpruntDAOImpl(LivreDAO livreDAO, MembreDAO membreDAO) {
        this.livreDAO = livreDAO;
        this.membreDAO = membreDAO;
    }

    /**
     * Insère un nouvel emprunt dans la base de données.
     * 
     * @param emprunt L'emprunt à insérer
     * @return L'emprunt inséré avec son ID généré
     * @throws Exception En cas d'erreur lors de l'insertion
     */
    @Override
    public Emprunt insert(Emprunt emprunt) throws Exception {
        String sql = "INSERT INTO emprunts (livre_id, membre_id, date_emprunt, date_retour_prevue, date_retour_reelle) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, emprunt.getLivreId());
            pstmt.setInt(2, emprunt.getMembreId());
            pstmt.setString(3, emprunt.getDateEmprunt());
            pstmt.setString(4, emprunt.getDateRetourPrevue());
            pstmt.setString(5, emprunt.getDateRetourReelle());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La création de l'emprunt a échoué, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emprunt.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création de l'emprunt a échoué, aucun ID obtenu.");
                }
            }
            
            livreDAO.updateDisponibilite(emprunt.getLivreId(), false);
            
            return emprunt;
        }
    }

    /**
     * Met à jour un emprunt existant dans la base de données.
     * 
     * @param emprunt L'emprunt à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    @Override
    public boolean update(Emprunt emprunt) throws Exception {
        String sql = "UPDATE emprunts SET livre_id = ?, membre_id = ?, date_emprunt = ?, date_retour_prevue = ?, date_retour_reelle = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, emprunt.getLivreId());
            pstmt.setInt(2, emprunt.getMembreId());
            pstmt.setString(3, emprunt.getDateEmprunt());
            pstmt.setString(4, emprunt.getDateRetourPrevue());
            pstmt.setString(5, emprunt.getDateRetourReelle());
            pstmt.setInt(6, emprunt.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprime un emprunt de la base de données par son ID.
     * 
     * @param id L'ID de l'emprunt à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la suppression
     */
    @Override
    public boolean delete(int id) throws Exception {
        Emprunt emprunt = findById(id);
        if (emprunt == null) {
            return false;
        }
        
        String sql = "DELETE FROM emprunts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0 && (emprunt.getDateRetourReelle() == null || emprunt.getDateRetourReelle().isEmpty())) {
                livreDAO.updateDisponibilite(emprunt.getLivreId(), true);
            }
            
            return affectedRows > 0;
        }
    }

    /**
     * Récupère un emprunt par son ID.
     * 
     * @param id L'ID de l'emprunt à récupérer
     * @return L'emprunt correspondant à l'ID, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public Emprunt findById(int id) throws Exception {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Emprunt emprunt = extractEmpruntFromResultSet(rs);
                    
                    Livre livre = livreDAO.findById(emprunt.getLivreId());
                    Membre membre = membreDAO.findById(emprunt.getMembreId());
                    
                    emprunt.setLivre(livre);
                    emprunt.setMembre(membre);
                    
                    return emprunt;
                }
            }
        }
        
        return null;
    }

    /**
     * Récupère tous les emprunts.
     * 
     * @return Une liste de tous les emprunts
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public List<Emprunt> findAll() throws Exception {
        String sql = "SELECT * FROM emprunts";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
        }
        
        return emprunts;
    }

    /**
     * Recherche des emprunts par ID de livre.
     * 
     * @param livreId L'ID du livre
     * @return Liste des emprunts correspondant au livre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Emprunt> findByLivreId(int livreId) throws Exception {
        String sql = "SELECT * FROM emprunts WHERE livre_id = ?";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, livreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Emprunt emprunt = extractEmpruntFromResultSet(rs);
                    
                    Livre livre = livreDAO.findById(emprunt.getLivreId());
                    Membre membre = membreDAO.findById(emprunt.getMembreId());
                    
                    emprunt.setLivre(livre);
                    emprunt.setMembre(membre);
                    
                    emprunts.add(emprunt);
                }
            }
        }
        
        return emprunts;
    }

    /**
     * Recherche des emprunts par ID de membre.
     * 
     * @param membreId L'ID du membre
     * @return Liste des emprunts correspondant au membre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Emprunt> findByMembreId(int membreId) throws Exception {
        String sql = "SELECT * FROM emprunts WHERE membre_id = ?";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, membreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Emprunt emprunt = extractEmpruntFromResultSet(rs);
                    
                    Livre livre = livreDAO.findById(emprunt.getLivreId());
                    Membre membre = membreDAO.findById(emprunt.getMembreId());
                    
                    emprunt.setLivre(livre);
                    emprunt.setMembre(membre);
                    
                    emprunts.add(emprunt);
                }
            }
        }
        
        return emprunts;
    }

    /**
     * Recherche des emprunts en cours (non retournés).
     * 
     * @return Liste des emprunts en cours
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Emprunt> findAllEnCours() throws Exception {
        String sql = "SELECT * FROM emprunts WHERE date_retour_reelle IS NULL OR date_retour_reelle = ''";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Emprunt emprunt = extractEmpruntFromResultSet(rs);
                
                Livre livre = livreDAO.findById(emprunt.getLivreId());
                Membre membre = membreDAO.findById(emprunt.getMembreId());
                
                emprunt.setLivre(livre);
                emprunt.setMembre(membre);
                
                emprunts.add(emprunt);
            }
        }
        
        return emprunts;
    }

    /**
     * Recherche des emprunts en retard.
     * 
     * @return Liste des emprunts en retard
     * @throws Exception En cas d'erreur lors de la recherche
     */
    @Override
    public List<Emprunt> findAllEnRetard() throws Exception {
        String dateActuelle = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        String sql = "SELECT * FROM emprunts WHERE (date_retour_reelle IS NULL OR date_retour_reelle = '') AND date_retour_prevue < ?";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dateActuelle);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Emprunt emprunt = extractEmpruntFromResultSet(rs);
                    
                    Livre livre = livreDAO.findById(emprunt.getLivreId());
                    Membre membre = membreDAO.findById(emprunt.getMembreId());
                    
                    emprunt.setLivre(livre);
                    emprunt.setMembre(membre);
                    
                    emprunts.add(emprunt);
                }
            }
        }
        
        return emprunts;
    }

    /**
     * Enregistre le retour d'un emprunt.
     * 
     * @param id L'ID de l'emprunt
     * @param dateRetour La date de retour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    @Override
    public boolean retournerEmprunt(int id, String dateRetour) throws Exception {
        String sql = "UPDATE emprunts SET date_retour_reelle = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dateRetour);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                Emprunt emprunt = findById(id);
                if (emprunt != null) {
                    livreDAO.updateDisponibilite(emprunt.getLivreId(), true);
                }
                return true;
            }
            
            return false;
        }
    }

    /**
     * Récupère les emprunts avec les informations complètes des livres et membres associés.
     * 
     * @return Liste des emprunts avec informations complètes
     * @throws Exception En cas d'erreur lors de la récupération
     */
    @Override
    public List<Emprunt> findAllWithDetails() throws Exception {
        String sql = "SELECT e.*, l.titre, l.auteur, m.nom, m.prenom " +
                     "FROM emprunts e " +
                     "JOIN livres l ON e.livre_id = l.id " +
                     "JOIN membres m ON e.membre_id = m.id";
        
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Emprunt emprunt = extractEmpruntFromResultSet(rs);
                
                Livre livre = livreDAO.findById(emprunt.getLivreId());
                Membre membre = membreDAO.findById(emprunt.getMembreId());
                
                emprunt.setLivre(livre);
                emprunt.setMembre(membre);
                
                emprunts.add(emprunt);
            }
        }
        
        return emprunts;
    }
    
    /**
     * Extrait un objet Emprunt d'un ResultSet.
     * 
     * @param rs Le ResultSet contenant les données de l'emprunt
     * @return Un objet Emprunt
     * @throws SQLException En cas d'erreur lors de l'extraction
     */
    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        emprunt.setLivreId(rs.getInt("livre_id"));
        emprunt.setMembreId(rs.getInt("membre_id"));
        emprunt.setDateEmprunt(rs.getString("date_emprunt"));
        emprunt.setDateRetourPrevue(rs.getString("date_retour_prevue"));
        emprunt.setDateRetourReelle(rs.getString("date_retour_reelle"));
        return emprunt;
    }
}
