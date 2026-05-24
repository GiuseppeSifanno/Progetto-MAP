package progetto.gioco.engine.model;

public abstract class BasePuzzle {
    protected String id;
    protected boolean risolto;
    
    /** 
     * @return boolean
     */
    public boolean isRisolto(){
        return this.risolto;
    }

    /**
     * @param input
     * @return
     */
    public abstract boolean risolvi(String input);
}
