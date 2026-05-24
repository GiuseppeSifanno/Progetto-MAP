package progetto.gioco.engine.model;

public abstract class BaseDialogo {
    private String idDialogo;
    private String testo;

    /** 
     * @return String
     */
    public String getIdDialogo(){
        return this.idDialogo;
    }

    /** 
     * @return String
     */
    public String getTesto(){
        return this.testo;
    }

    public abstract int getNumeroScelte();
}
