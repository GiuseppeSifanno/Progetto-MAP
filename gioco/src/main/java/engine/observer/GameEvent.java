package engine.observer;

/**
 * Classe che rappresenta un evento del gioco.
 */
public class GameEvent {
    /** Tipo di evento. */
    private TipoEvento tipo;
    /** Contenuto che deve contenere l'evento, qualsiasi oggetto. */
    private Object payload;
    /** Timestamp dell'evento. */
    private final Long timestamp;

    /**
     * Costruttore di base.
     */
    public GameEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Costruttore con parametri.
     * @param tipo Tipo di evento
     * @param payload Contenuto che deve contenere l'evento, qualsiasi oggetto
     */
    public GameEvent(TipoEvento tipo, Object payload) {
        this.tipo = tipo;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Ritorna il tipo di evento.
     * @return TipoEvento
     */
    public TipoEvento getTipo(){
        return this.tipo;
    }

    /**
     * Setta un tipo di evento.
     * @param tipo Tipo di evento
     */
    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    /**
     * Ritorna il contenuto che deve contenere l'evento, qualsiasi oggetto.
     * @return Object
     */
    public Object getPayload(){
        return this.payload;
    }

    /**
     * Setta il contenuto che deve contenere l'evento, qualsiasi oggetto.
     * @param payload Contenuto che deve contenere l'evento, qualsiasi oggetto
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    /**
     * Ritorna il timestamp dell'evento.
     * @return Long
     */
    public Long timestamp(){
        return this.timestamp;
    }
}
