package progetto.gioco.game.model;

public class Scelta {
    private String idScelta;
    private String testo;
    private String next;

    public Scelta(String idScelta, String testo, String next) {
        this.idScelta = idScelta;
        this.testo = testo;
        this.next = next;
    }

    public Scelta(String idScelta, String testo) {
        this.idScelta = idScelta;
        this.testo = testo;
        this.next = null;
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
        return testo;
    }
    /** 
     * @return String
     */
    public String getNext() {
        return next;
    }
}