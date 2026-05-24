package progetto.gioco.engine.model;

public abstract class BaseScelta {
    protected String idScelta;
    protected String testo;
    protected String next;

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
