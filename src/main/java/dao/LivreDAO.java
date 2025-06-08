package dao;

import models.Livre;
import java.util.List;

/**
 * Interface DAO spécifique pour l'entité Livre.
 */
public interface LivreDAO extends DAO<Livre> {
    
    /**
     * Recherche des livres par titre.
     * 
     * @param titre Le titre à rechercher
     * @return Liste des livres correspondant au titre
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Livre> findByTitre(String titre) throws Exception;
    
    /**
     * Recherche un livre par ISBN.
     * 
     * @param isbn L'ISBN à rechercher
     * @return Le livre correspondant à l'ISBN, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la recherche
     */
    Livre findByISBN(String isbn) throws Exception;
    
    /**
     * Recherche des livres par auteur.
     * 
     * @param auteur L'auteur à rechercher
     * @return Liste des livres correspondant à l'auteur
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Livre> findByAuteur(String auteur) throws Exception;
    
    /**
     * Récupère tous les livres disponibles.
     * 
     * @return Liste des livres disponibles
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Livre> findAllAvailable() throws Exception;
    
    /**
     * Met à jour la disponibilité d'un livre.
     * 
     * @param id L'ID du livre
     * @param disponible La nouvelle valeur de disponibilité
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    boolean updateDisponibilite(int id, boolean disponible) throws Exception;
}
