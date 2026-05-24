package progetto.gioco.engine.model;

public abstract class BaseScelta {
    private String idScelta;
    private String testo;
    private String next;
    
    public String getIdScelta() {
        return this.idScelta;
    }

    public String getTesto() {
        return this.testo;
    }

    public String getNext() {
        return this.next;
    }
}
