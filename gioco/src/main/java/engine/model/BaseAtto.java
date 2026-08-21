package engine.model;

import java.util.Collections;
import java.util.Map;

/**
 * Classe astratta che rappresenta un atto.
 * @param <D> tipo generico per un dialogo, in questo caso BaseDialogo
 */
public abstract class BaseAtto<D extends BaseDialogo> extends BaseEntity {
    /** Dialogo iniziale. */
    protected String dialogoIniziale;
    /** Personaggi presenti nell'atto. */
    protected Map<String, Personaggio> personaggi;
    /** Dialoghi presenti nell'atto. */
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
     * Restituisce l'id del dialogo iniziale.
     * @return Id dialogo iniziale
     */
    public String getDialogoIniziale(){
        return this.dialogoIniziale;
    }

    /**
     * Imposta l'id del dialogo iniziale.
     * @param dialogoIniziale Id dialogo iniziale
     */
    public void setDialogoIniziale(String dialogoIniziale) {
        this.dialogoIniziale = dialogoIniziale;
    }

    /**
     * Restituisce la mappa di personaggi.
     * @return Mappa di personaggi
     */
    public Map<String, Personaggio> getPersonaggi() {
        return Collections.unmodifiableMap(personaggi);
    }

    /**
     * Restituisce un personaggio dall'id.
     * @param idPersonaggio Id personaggio
     * @return Personaggio
     */
    public Personaggio getPersonaggio(String idPersonaggio) {
        return personaggi.get(idPersonaggio);
    }

    /**
     * Restituisce la mappa di dialoghi.
     * @return Mappa di dialoghi
     */
    public Map<String, D> getDialoghi() { return Collections.unmodifiableMap(dialoghi); }
}
