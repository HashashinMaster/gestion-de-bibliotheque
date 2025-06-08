package dao;

import models.Membre;
import java.util.List;

/**
 * Interface DAO spécifique pour l'entité Membre.
 */
public interface MembreDAO extends DAO<Membre> {
    
    /**
     * Recherche des membres par nom.
     * 
     * @param nom Le nom à rechercher
     * @return Liste des membres correspondant au nom
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Membre> findByNom(String nom) throws Exception;
    
    /**
     * Recherche un membre par email.
     * 
     * @param email L'email à rechercher
     * @return Le membre correspondant à l'email, ou null si non trouvé
     * @throws Exception En cas d'erreur lors de la recherche
     */
    Membre findByEmail(String email) throws Exception;
    
    /**
     * Recherche des membres par nom complet (nom et prénom).
     * 
     * @param nom Le nom à rechercher
     * @param prenom Le prénom à rechercher
     * @return Liste des membres correspondant au nom complet
     * @throws Exception En cas d'erreur lors de la recherche
     */
    List<Membre> findByNomComplet(String nom, String prenom) throws Exception;
}
