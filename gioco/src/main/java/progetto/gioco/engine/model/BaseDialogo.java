package progetto.gioco.engine.model;

public abstract class BaseDialogo {
    protected String idDialogo;
    protected String testo;

    /** 
     * @return String
     */
    public String getTesto(){
        return this.testo;
    }

    public abstract int getNumeroScelte();
}
