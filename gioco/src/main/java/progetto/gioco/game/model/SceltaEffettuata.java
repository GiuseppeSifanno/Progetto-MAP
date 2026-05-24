package progetto.gioco.game.model;

public class SceltaEffettuata {
    private String idScelta;
    private String idDialogo;


    public SceltaEffettuata(String idScelta, String idDialogo) {
        this.idScelta = idScelta;
        this.idDialogo = idDialogo;
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
    public String getIdDialogo() {
        return idDialogo;
    }
}
