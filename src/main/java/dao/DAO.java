package dao;

import java.util.List;

/**
 * Interface générique pour les opérations CRUD.
 * 
 * @param <T> Type de l'entité
 */
public interface DAO<T> {
    
    /**
     * Insère une nouvelle entité dans la base de données.
     * 
     * @param entity L'entité à insérer
     * @return L'entité insérée avec son ID généré
     * @throws Exception En cas d'erreur lors de l'insertion
     */
    T insert(T entity) throws Exception;
    
    /**
     * Met à jour une entité existante dans la base de données.
     * 
     * @param entity L'entité à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la mise à jour
     */
    boolean update(T entity) throws Exception;
    
    /**
     * Supprime une entité de la base de données par son ID.
     * 
     * @param id L'ID de l'entité à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws Exception En cas d'erreur lors de la suppression
     */
    boolean delete(int id) throws Exception;
    
    /**
     * Récupère une entité par son ID.
     * 
     * @param id L'ID de l'entité à récupérer
     * @return L'entité correspondant à l'ID, ou null si non trouvée
     * @throws Exception En cas d'erreur lors de la récupération
     */
    T findById(int id) throws Exception;
    
    /**
     * Récupère toutes les entités.
     * 
     * @return Une liste de toutes les entités
     * @throws Exception En cas d'erreur lors de la récupération
     */
    List<T> findAll() throws Exception;
}
