package engine.model;

/**
 * Classe astratta che rappresenta un puzzle.
 */
public abstract class BasePuzzle extends BaseEntity {
    protected boolean risolto;

    /**
     * Costruttore di base.
     * @param id Id puzzle
     * @param risolto Stato risoluzione
     */
    public BasePuzzle(String id, boolean risolto) {
        super(id);
        this.risolto = risolto;
    }

    /**
     * Ritorna lo stato risoluzione.
     * @return boolean
     */
    public boolean isRisolto(){
        return this.risolto;
    }

    /**
     * @implNote Questo metodo deve essere implementato da ogni sottoclasse poichè i puzzle
     * hanno comportamenti diversi e tipi di soluzione diversi per essere risolti.
     * @param input Stringa da confrontare con la soluzione
     * @return true se risolto, false altrimenti
     */
    public abstract boolean risolvi(String input);
}
