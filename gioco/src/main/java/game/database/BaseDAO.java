package game.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.database.DBManager;

public abstract class BaseDAO<T> {
    protected final DBManager dbManager;

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