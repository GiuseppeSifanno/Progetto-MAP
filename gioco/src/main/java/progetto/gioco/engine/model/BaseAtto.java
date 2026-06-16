package progetto.gioco.engine.model;

import java.util.Map;

public abstract class BaseAtto{
    protected String idAtto;
    protected String dialogoIniziale;

    public BaseAtto(String idAtto, String dialogoIniziale) {
        this.idAtto = idAtto;
        this.dialogoIniziale = dialogoIniziale;
    }

    /** 
     * @return String
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    public abstract Map<?, ?> getDialoghi();
}