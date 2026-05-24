package progetto.gioco.engine.model;

import java.util.Map;

public abstract class BaseAtto {
    private String idAtto;
    private String dialogoIniziale;
    
    /** 
     * @return String
     */
    public String getIdAtto(){
        return this.idAtto;
    }

    /** 
     * @return String
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    public abstract Map<String, String> getDialoghi();
}