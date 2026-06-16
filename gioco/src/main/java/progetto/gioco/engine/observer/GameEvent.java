package progetto.gioco.engine.observer;

public class GameEvent {
    private TipoEvento tipo;
    private Object payload;
    private Long timestamp;

    public GameEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public GameEvent(TipoEvento tipo, Object payload) {
        this.tipo = tipo;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * @return TipoEvento
     */
    public TipoEvento getTipo(){
        return this.tipo;
    }

    /**
     * @param tipo
     */
    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    /**
     * @return Object
     */
    public Object getPayload(){
        return this.payload;
    }

    /**
     * @param payload
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    /**
     * @return Long
     */
    public Long timestamp(){
        return this.timestamp;
    }
}
