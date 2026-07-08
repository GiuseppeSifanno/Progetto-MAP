package progetto.gioco.engine.model;

/**
 * Classe astratta che rappresenta una scelta.
 */
public abstract class BaseScelta extends BaseEntity{
    protected String idScelta;
    protected String testo;

    /**
     * Costruttore di base.
     * @param idScelta id Scelta
     * @param testo testo contenuto in una scelta
     */
    public BaseScelta(String idScelta, String testo) {
        super(idScelta);
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
