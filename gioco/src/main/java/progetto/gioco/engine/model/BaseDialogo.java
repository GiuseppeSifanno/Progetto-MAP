package progetto.gioco.engine.model;

public abstract class BaseDialogo {
    protected String idDialogo;
    protected String testo;

    public BaseDialogo(String idDialogo, String testo) {
        this.idDialogo = idDialogo;
        this.testo = testo;
    }

    /** 
     * @return String
     */
    public String getTesto(){
        return this.testo;
    }

    public abstract int getNumeroScelte();

    /** 
     * @return String
     */
    public String getIdDialogo() {
        return idDialogo;
    }
}
