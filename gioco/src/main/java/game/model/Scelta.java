package game.model;

import engine.model.BaseScelta;

public class Scelta extends BaseScelta {
    private final String next;

    /**
     * Crea una nuova istanza di Scelta.
     * @param idScelta identificativo univoco della scelta
     * @param testo testo della scelta
     * @param next identificativo univoco della scelta successiva
     */
    public Scelta(String idScelta, String testo, String next) {
        super(idScelta, testo);
        this.next = next;
    }

    /**
     * Crea una nuova istanza di Scelta senza scelta successiva.
     * @param idScelta identificativo univoco della scelta
     * @param testo testo della scelta
     * @implNote Questo costruttore crea una scelta senza scelta successiva, impostando il campo next a null.
     */
    public Scelta(String idScelta, String testo) {
        super(idScelta, testo);
        this.next = null;
    }

    /** 
     * @return String
     */
    public String getNext() {
        return this.next;
    }
}