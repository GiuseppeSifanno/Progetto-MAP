package progetto.gioco.engine.model;

public abstract class BaseScelta {
    protected String idScelta;
    protected String testo;

    public BaseScelta(String idScelta, String testo) {
        this.idScelta = idScelta;
        this.testo = testo;
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
}
