package engine.database;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia DAO per il salvataggio.
 * @param <T> tipo generico per un oggetto
 */
public interface SalvataggioDAO<T> {
    /**
     * Salva un oggetto nel database.
     * @param stato oggetto da salvare
     * @param idSlot numero slot del salvataggio
     * @throws SQLException se si verifica un errore di connessione al database
     */
    void salva(T stato, int idSlot) throws SQLException;

    /**
     * Carica un oggetto dal database.
      * @param idSlot numero slot del salvataggio
     * @return oggetto caricato
     * @throws SQLException se si verifica un errore di connessione al database
     */
    T carica(int idSlot) throws SQLException;

    /**
     * Restituisce una lista di slot disponibili.
     * @return lista di slot disponibili
     */
    List<Integer> listaSlotDisponibili();

    /**
     * Elimina un salvataggio dal database.
     * @param idSlot numero slot del salvataggio
     */
    void elimina(int idSlot);
}