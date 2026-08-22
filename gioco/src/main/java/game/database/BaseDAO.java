package game.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.database.DBManager;

/**
 * Classe astratta che gestisce le operazioni CRUD su un database.
 * @param <T> tipo generico per un oggetto
 */
public abstract class BaseDAO<T> {
    /** Manager che gestisce la connessione al database. */
    protected final DBManager dbManager;

    /**
     * Costruttore di base.
     * @param dbManager Manager che gestisce la connessione al database
     */
    protected BaseDAO(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Converte una riga di ResultSet nell'entità concreta T.
     * @param rs ResultSet posizionato sulla riga corrente
     * @return istanza di T
     */
    protected abstract T mapRow(ResultSet rs) throws SQLException;
}