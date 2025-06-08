package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Système d'événements simple pour la communication entre contrôleurs.
 */
public class EventSystem {
    
    // Singleton
    private static EventSystem instance;
    
    private final Map<String, List<Consumer<Object>>> eventListeners = new HashMap<>();
    
    private EventSystem() {
        // Constructeur privé pour le singleton
    }
    
    /**
     * Obtient l'instance unique du système d'événements.
     * 
     * @return L'instance du système d'événements
     */
    public static synchronized EventSystem getInstance() {
        if (instance == null) {
            instance = new EventSystem();
        }
        return instance;
    }
    
    /**
     * S'abonne à un événement.
     * 
     * @param eventName Nom de l'événement
     * @param listener Fonction à appeler lors de l'événement
     */
    public void subscribe(String eventName, Consumer<Object> listener) {
        eventListeners.computeIfAbsent(eventName, k -> new ArrayList<>()).add(listener);
    }
    
    /**
     * Déclenche un événement.
     * 
     * @param eventName Nom de l'événement
     * @param data Données à passer aux abonnés (peut être null)
     */
    public void publish(String eventName, Object data) {
        List<Consumer<Object>> listeners = eventListeners.get(eventName);
        if (listeners != null) {
            for (Consumer<Object> listener : listeners) {
                listener.accept(data);
            }
        }
    }
    
    /**
     * Se désabonne d'un événement.
     * 
     * @param eventName Nom de l'événement
     * @param listener Fonction à désabonner
     */
    public void unsubscribe(String eventName, Consumer<Object> listener) {
        List<Consumer<Object>> listeners = eventListeners.get(eventName);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }
}
