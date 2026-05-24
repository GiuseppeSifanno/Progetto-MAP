package progetto.gioco.engine.model;

public abstract class BaseScelta {
    private String idScelta;
    private String testo;
    private String next;
    
    /** 
     * @return String
     */
    public String getIdScelta() {
        return this.idScelta;
    }

    /** 
     * @return String
     */
    public String getTesto() {
        return this.testo;
    }

    /** 
     * @return String
     */
    public String getNext() {
        return this.next;
    }
}
