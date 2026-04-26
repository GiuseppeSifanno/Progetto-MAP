package progetto.gioco.model;

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

    public String getIdScelta() {
        return idScelta;
    }
    public String getTesto() {
        return testo;
    }
    public String getNext() {
        return next;
    }
}