package engine.model;

import java.util.Collections;
import java.util.List;

/**
 * Classe astratta che rappresenta un dialogo.
 */
public abstract class BaseDialogo extends BaseEntity {
    /** Lista di battute. */
    protected final List<Battuta> battute;

    /**
     * Costruttore di base.
     * @param battute Lista di battute
     */
    public BaseDialogo(String idDialogo, List<Battuta> battute) {
        super(idDialogo);
        this.battute = battute;
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
    public List<Battuta> getBattute() {
        return Collections.unmodifiableList(this.battute);
    }
}
