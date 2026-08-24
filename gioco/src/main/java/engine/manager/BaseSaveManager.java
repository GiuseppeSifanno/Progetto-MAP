package engine.manager;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe astratta che gestisce i salvataggi.
 */
public abstract class BaseSaveManager extends BaseManager {
    /**
     * Salva un oggetto in un slot dei salvataggi.
     * @param stato Oggetto da salvare
     * @param idSlot slot di salvataggio
     */
    public abstract void salva(Object stato, int idSlot) throws SQLException;

    /**
     * Carica un oggetto dai salvataggi.
     * @param idSlot numero slot del salvataggio
     * @return Oggetto
     */
    public abstract Object carica(int idSlot) throws SQLException;

    /**
     * Elimina un salvataggio.
     * @param idSlot numero slot del salvataggio
     */
    public abstract void elimina(int idSlot) throws SQLException;

    /**
     * Restituisce una lista dei salvataggi.
     * @return lista degli slot dei salvataggi disponibili
     */
    public abstract List<Integer> listaSalvataggi();
}
