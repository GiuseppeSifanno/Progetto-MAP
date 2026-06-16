package progetto.gioco.engine.model;

public abstract class BasePuzzle extends BaseEntity {
    protected boolean risolto;
    
    public BasePuzzle(String id, boolean risolto) {
        super(id);
        this.risolto = risolto;
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
