package progetto.gioco.engine.model;

import java.util.Map;

public abstract class BaseAtto {
    protected String idAtto;
    protected String dialogoIniziale;

    /** 
     * @return String
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    public abstract Map<String, String> getDialoghi();
}