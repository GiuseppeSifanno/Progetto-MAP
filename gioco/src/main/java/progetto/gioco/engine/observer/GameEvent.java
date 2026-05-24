package progetto.gioco.engine.observer;

public class GameEvent {
    private TipoEvento tipo;
    private Object payload;
    private Long timestamp;

    public TipoEvento getTipo(){
        return this.tipo;
    }

    public Object getPayload(){
        return this.payload;
    }

    public Long timestamp(){
        return this.timestamp;
    }
}
