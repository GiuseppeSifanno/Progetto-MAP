package progetto.gioco.model;

import java.util.Map;

public class Atto {
    private String idAtto;
    //map dei dialoghi di un determinato atto
    private Map<String, Dialogo> dialoghi;
    //id dialogo iniziale
    private String dialogoIniziale;


    public Atto(String idAtto, Map<String, Dialogo> dialoghi, String dialogoIniziale) {
        this.idAtto = idAtto;
        this.dialoghi = dialoghi;
        this.dialogoIniziale = dialogoIniziale;
    }

    public String getIdAtto() {
        return idAtto;
    }

    public Map<String, Dialogo> getDialoghi() {
        return dialoghi;
    }

    public String getDialogoIniziale() {
        return dialogoIniziale;
    }
}
