package engine.model;

import java.util.Collections;
import java.util.Map;

/**
 * Classe astratta che rappresenta un atto.
 */
public abstract class BaseAtto<D extends BaseDialogo> extends BaseEntity {
    protected String dialogoIniziale;
    protected Map<String, Personaggio> personaggi;
    protected Map<String, D> dialoghi;

    /**
     * Costruttore di base.
     * @param idAtto Id atto
     * @param dialoghi Mappa di dialoghi
     * @param dialogoIniziale Id dialogo iniziale
     */
    public BaseAtto(String idAtto, String dialogoIniziale, Map<String, Personaggio> personaggi, Map<String, D> dialoghi) {
        super(idAtto);
        this.dialogoIniziale = dialogoIniziale;
        this.personaggi = personaggi;
        this.dialoghi = dialoghi;
    }

    /**
     * @return String
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    /**
      * @param dialogoIniziale id dialogo iniziale
     */
    public void setDialogoIniziale(String dialogoIniziale) {
        this.dialogoIniziale = dialogoIniziale;
    }

    public Map<String, Personaggio> getPersonaggi() {
        return Collections.unmodifiableMap(personaggi);
    }

    public Personaggio getPersonaggio(String idPersonaggio) {
        return personaggi.get(idPersonaggio);
    }

    /**
     * Restituisce la mappa di dialoghi.
     * @return Mappa di dialoghi
     */
    public Map<String, D> getDialoghi() { return Collections.unmodifiableMap(dialoghi); }
}
