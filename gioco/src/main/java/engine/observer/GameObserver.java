package engine.observer;

/** Interfaccia per gli observer del gioco. */
public interface GameObserver {
    /**
     * Metodo che viene chiamato quando viene generato un evento.
     * @param evento Evento generato
     */
    void onEvent(GameEvent evento);
}
