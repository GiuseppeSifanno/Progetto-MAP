package engine.model;

/**
 * Classe astratta che rappresenta un dialogo.
 */
public abstract class BaseDialogo extends BaseEntity {
    protected final String idDialogo;
    protected final String testo;

    /**
     * Costruttore di base.
     * @param idDialogo id Dialogo
     * @param testo Testo del dialogo
     */
    public BaseDialogo(String idDialogo, String testo) {
        super(idDialogo);
        this.idDialogo = idDialogo;
        this.testo = testo;
    }

    /** 
     * @return String
     */
    public String getTesto(){
        return this.testo;
    }

    /**
     * Restituisce il numero di scelte disponibili nel dialogo.
     * @implNote La sua implementazione dipende dalla classe che estende BaseDialogo.
     * Non tutti i dialoghi hanno scelte quindi inserirlo in una classe astratta non ha senso.
     * @return numero di scelte
     */
    public abstract int getNumeroScelte();

    /** 
     * @return String
     */
    public String getIdDialogo() {
        return idDialogo;
    }
}
