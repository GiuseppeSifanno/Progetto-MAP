package progetto.gioco.engine.model;

public abstract class BaseDialogo {
    private String idDialogo;
    private String testo;

    public String getIdDialogo(){
        return this.idDialogo;
    }

    public String getTesto(){
        return this.testo;
    }

    public abstract int getNumeroScelte();
}
