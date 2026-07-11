package engine.model;

/**
 * Classe astratta che rappresenta una scelta.
 */
public abstract class BaseScelta extends BaseEntity {
    protected final String testo;

    /**
     * Costruttore di base.
     * @param idScelta id Scelta
     * @param testo testo contenuto in una scelta
     */
    public BaseScelta(String idScelta, String testo) {
        super(idScelta);
        this.testo = testo;
    }

    /** 
     * @return String
     */
    public String getTesto() {
        return this.testo;
    }
}
