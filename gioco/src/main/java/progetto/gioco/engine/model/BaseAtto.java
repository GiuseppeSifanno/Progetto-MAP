package progetto.gioco.engine.model;

import java.util.Map;

/**
 * Classe astratta che rappresenta un atto.
 */
public abstract class BaseAtto<D extends BaseDialogo> extends BaseEntity {
    protected Map<String, D> dialoghi;
    protected String dialogoIniziale;
    protected String idAtto;

    /**
     * Costruttore di base.
     * @param idAtto Id atto
     * @param dialoghi Mappa di dialoghi
     * @param dialogoIniziale Id dialogo iniziale
     */
    public BaseAtto(String idAtto, Map<String, D> dialoghi, String dialogoIniziale) {
        super(idAtto);
        this.idAtto = idAtto;
        this.dialoghi = dialoghi;
        this.dialogoIniziale = dialogoIniziale;
    }

    public String getIdAtto() { return idAtto; }

    public void setIdAtto(String idAtto) { this.idAtto = idAtto; }

    /**
     * Restituisce la mappa di dialoghi.
     * @return Mappa di dialoghi
     */
    public Map<String, D> getDialoghi() { return dialoghi; }

    public void setDialoghi(Map<String, D> dialoghi) { this.dialoghi = dialoghi; }

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
