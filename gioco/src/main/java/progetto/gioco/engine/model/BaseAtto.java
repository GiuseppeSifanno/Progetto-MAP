package progetto.gioco.engine.model;

import java.util.Map;

import progetto.gioco.game.model.Dialogo;

public class BaseAtto {
    protected String idAtto;
    protected Map<String, Dialogo> dialoghi;
    protected String dialogoIniziale;

    public BaseAtto(String idAtto, Map<String, Dialogo> dialoghi, String dialogoIniziale) {
        this.idAtto = idAtto;
        this.dialoghi = dialoghi;
        this.dialogoIniziale = dialogoIniziale;
    }

    public String getIdAtto() {
        return idAtto;
    }

    public void setIdAtto(String idAtto) {
        this.idAtto = idAtto;
    }

    public Map<String, Dialogo> getDialoghi() {
        return dialoghi;
    }

    public void setDialoghi(Map<String, Dialogo> dialoghi) {
        this.dialoghi = dialoghi;
    }

    /** 
     * @return String
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    public void setDialogoIniziale(String dialogoIniziale) {
        this.dialogoIniziale = dialogoIniziale;
    }
}
