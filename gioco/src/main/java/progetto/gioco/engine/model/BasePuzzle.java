package progetto.gioco.engine.model;

public abstract class BasePuzzle {
    private String id;
    private boolean risolto;
    
    public String getId(){
        return this.id;
    }

    public boolean isRisolto(){
        return this.risolto;
    }

    public abstract boolean risolvi(String input);
}
