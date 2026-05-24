package progetto.gioco.engine.model;

import java.util.Map;

public abstract class BaseAtto {
    private String idAtto;
    private String dialogoIniziale;
    
    public String getIdAtto(){
        return this.idAtto;
    }

    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    public abstract Map<String, String> getDialoghi();
}