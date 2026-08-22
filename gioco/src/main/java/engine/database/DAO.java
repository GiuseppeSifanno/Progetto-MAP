package engine.database;

import java.util.List;

/**
 * Interfaccia a cui si rifà ogni DAO
 * @param <T> tipo generico per un oggetto
 * @param <ID> tipo generico per un identificativo
 */
public interface DAO <T, ID>{
    /**
     * Cerca un oggetto in base all'id
     * @param id id dell'oggetto da cercare
     * @return T tipo generico per un oggetto trovato nel database
     */
    T findById(ID id);

    /**
     * Cerca tutti gli oggetti presenti nel database
     * @return Lista di oggetti trovati
     */
    List<T> findAll();

    /**
     * Salva un oggetto nel database
     * @param entity oggetto da salvare
     */
    void save(T entity);

    /**
     * Elimina un oggetto dal database
     * @param id id dell'oggetto da eliminare
     */
    void delete(ID id);
}

