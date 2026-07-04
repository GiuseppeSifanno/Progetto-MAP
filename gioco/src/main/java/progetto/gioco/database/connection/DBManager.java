package progetto.gioco.database.connection;

import progetto.gioco.engine.manager.BaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Gestore del database
 */
public class DBManager extends BaseManager {
    private static final String URL = "jdbc:h2:file:./database/game";
    private static final String USER = "sa";
    private static final String PASS = "";

    @Override
    public void init() {
        if (!databaseExists()) {
            runSchema("./db/schema.sql");
        }
    }

    @Override
    public void reset() {
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Verifica se il database esiste
     * @return true se esiste, false altrimenti
     */
    private boolean databaseExists() {
        File db = new File("./db/game.mv.db");
        return db.exists();
    }

    /**
     * Esegue lo schema del database che costruisce tutte le tabelle
     * @param resourcePath percorso del file sql
     */
    public void runSchema(String resourcePath) {
        String sql;
        try {
            sql = Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath)).getFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String query : sql.split(";")) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}