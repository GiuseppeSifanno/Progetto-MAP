package game.model;

import engine.model.BaseScelta;

public class Scelta extends BaseScelta {
    private final String next;

    public Scelta(String idScelta, String testo, String next) {
        super(idScelta, testo);
        this.next = next;
    }

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