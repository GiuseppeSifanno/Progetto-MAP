package progetto.gioco.engine.model;

import java.util.Map;
import java.util.UUID;

import progetto.gioco.game.model.Dialogo;

//TODO renderla abstract
public abstract class BaseAtto extends BaseEntity {
    protected String idAtto;
    protected Map<String, Dialogo> dialoghi;
    protected String dialogoIniziale;

    public BaseAtto(String idAtto, Map<String, Dialogo> dialoghi, String dialogoIniziale) {
        super(idAtto);
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
