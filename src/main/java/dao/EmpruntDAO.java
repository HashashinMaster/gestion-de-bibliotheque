package dao;

import models.Emprunt;
import java.util.List;

/**
 * Interface DAO spécifique pour l'entité Emprunt.
 */
public interface EmpruntDAO extends DAO<Emprunt> {
    
    /**
     * Recherche des emprunts par ID de livre.
     * 
     * @param livreId L'ID du livre
     * @return Liste des emprunts correspondant au livre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Emprunt> findByLivreId(int livreId) throws Exception;
    
    /**
     * Recherche des emprunts par ID de membre.
     * 
     * @param membreId L'ID du membre
     * @return Liste des emprunts correspondant au membre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Emprunt> findByMembreId(int membreId) throws Exception;
    
    /**
     * Recherche des emprunts en cours (non retournés).
     * 
     * @return Liste des emprunts en cours
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Emprunt> findAllEnCours() throws Exception;
    
    /**
     * Recherche des emprunts en retard.
     * 
     * @return Liste des emprunts en retard
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Emprunt> findAllEnRetard() throws Exception;
    
    /**
     * Enregistre le retour d'un emprunt.
     * 
     * @param id L'ID de l'emprunt
     * @param dateRetour La date de retour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    boolean retournerEmprunt(int id, String dateRetour) throws Exception;
    
    /**
     * Récupère les emprunts avec les informations complètes des livres et membres associés.
     * 
     * @return Liste des emprunts avec informations complètes
     * @throws Exception En cas d'erreur lors de la récupération
     */
    List<Emprunt> findAllWithDetails() throws Exception;
}
