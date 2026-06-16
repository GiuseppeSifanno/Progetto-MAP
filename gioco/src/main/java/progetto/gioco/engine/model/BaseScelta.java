package progetto.gioco.engine.model;

public abstract class BaseScelta extends BaseEntity {
    protected String idScelta;
    protected String testo;
    protected String next;

    public BaseScelta(String id, String idScelta, String testo, String next) {
        super(id);
        this.idScelta = idScelta;
        this.testo = testo;
        this.next = next;
    }

    /** 
     * @return String
     */
    public String getIdScelta() {
        return idScelta;
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
