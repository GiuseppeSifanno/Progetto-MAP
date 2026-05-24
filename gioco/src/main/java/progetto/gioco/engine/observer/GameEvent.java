package progetto.gioco.engine.observer;

public class GameEvent {
    private TipoEvento tipo;
    private Object payload;
    private Long timestamp;

    /** 
     * @return TipoEvento
     */
    public TipoEvento getTipo(){
        return this.tipo;
    }

    /** 
     * @return Object
     */
    public Object getPayload(){
        return this.payload;
    }

    /** 
     * @return Long
     */
    public Long timestamp(){
        return this.timestamp;
    }
}
