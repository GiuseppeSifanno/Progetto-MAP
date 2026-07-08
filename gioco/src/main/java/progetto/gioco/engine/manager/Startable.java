package progetto.gioco.engine.manager;

/**
 * Interfaccia che definisce i metodi principali del flusso di gioco
 */
public interface Startable {
    /**
     * Avvia il processo principale del flusso di gioco.
     * Questo metodo è responsabile di inizializzare e preparare tutti i componenti
     * necessari per avviare l'esecuzione del gioco.
     * @see BaseGameManager BaseGameManager
     */
    void start();

    /**
     * Interrompe il gioco, quindi la GUI e resetta tutti i manager
     */
    void stop();

    /**
     * Verifica se il gioco è in esecuzione
     * @return true se è in esecuzione, false altrimenti
     */
    boolean isRunning();
}
