package engine.model;

import java.util.List;

/**
 * Classe astratta che rappresenta un dialogo.
 */
public abstract class BaseDialogo extends BaseEntity {
    protected final List<Battuta> battuta;

    /**
     * Costruttore di base.
     * @param battuta Lista di battute
     */
    public BaseDialogo(String idDialogo, List<Battuta> battuta) {
        super(idDialogo);
        this.battuta = battuta;
    }

    /**
     * Restituisce il numero di scelte disponibili nel dialogo.
     * @implNote La sua implementazione dipende dalla classe che estende BaseDialogo.
     * Non tutti i dialoghi hanno scelte quindi inserirlo in una classe astratta non ha senso.
     * @return numero di scelte
     */
    public abstract int getNumeroScelte();

    /**
     * Restituisce la lista di battute
     * @return lista di battute
     */
    public abstract List<Battuta> getBattuta();

    public List<Battuta> getBattute() {
        return this.battuta;
    }
}
