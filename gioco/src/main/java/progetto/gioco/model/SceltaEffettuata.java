package progetto.gioco.model;

public class SceltaEffettuata {
    private String idScelta;
    private String idDialogo;


    public SceltaEffettuata(String idScelta, String idDialogo) {
        this.idScelta = idScelta;
        this.idDialogo = idDialogo;
    }

    public String getIdScelta() {
        return idScelta;
    }

    public String getIdDialogo() {
        return idDialogo;
    }
}
