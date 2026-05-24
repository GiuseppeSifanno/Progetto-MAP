package progetto.gioco.engine.model;

public abstract class BasePuzzle {
    private String id;
    private boolean risolto;
    
    /** 
     * @return String
     */
    public String getId(){
        return this.id;
    }

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
