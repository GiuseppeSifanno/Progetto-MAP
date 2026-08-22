package engine.model;

import java.util.Collections;
import java.util.Map;

/**
 * Classe astratta che rappresenta una zona.
 * @param <I> tipo generico per un'interazione, in questo caso BaseInterazione
 */
public abstract class BaseZona<I extends BaseInterazione> {
    /** Id della zona. */
    protected final String idZona;
    /** Map che contiene tutte le interazioni. */
    protected final Map<String, I> interazioni;

    /**
     * Costruttore di base.
     * @param idZona Id zona
     * @param interazioni Map che contiene tutte le interazioni
     */
    public BaseZona(String idZona, Map<String, I> interazioni) {
        this.idZona = idZona;
        this.interazioni = interazioni;
    }

    /**
     * Restituisce l'id della zona.
     * @return String
     */
    public String getIdZona() { return idZona; }

    /**
     * Restituisce tutte le interazioni.
     * @return Map di interazioni
     */
    public Map<String, I> getInterazioni() { return Collections.unmodifiableMap(interazioni); }
}
