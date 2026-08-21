package engine.database;

import engine.manager.BaseManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Classe che gestisce la connessione al database.
 */
public class DBManager extends BaseManager {
    private final String url;
    private final String user;
    private final String pass;
    private final String schemaPath;
    private final String dbFileName;

    /**
     * Costruttore della classe.
     * @param configPath percorso del file di configurazione
     */
    public DBManager(String configPath) {
        Properties props = loadConfig(configPath);
        this.dbFileName = props.getProperty("db.name", "game");
        this.schemaPath = props.getProperty("db.schema", "db/schema.sql");
        this.user = props.getProperty("db.user", "sa");
        this.pass = props.getProperty("db.pass", "");
        this.url = "jdbc:h2:file:./database/" + dbFileName;

        // Mostra il percorso in cui si trova il file necessario al DB per funzionare
        System.out.println("DB path: " + new File("./database/" + dbFileName + ".mv.db").getAbsolutePath());
    }

    /**
     * Carica le configurazioni dal file di configurazione.
     * @param configPath percorso del file di configurazione
     * @return Configurazioni nell'oggetto Properties
     */
    private Properties loadConfig(String configPath) {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (is != null) props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere " + configPath, e);
        }
        return props;
    }

    @Override
    public void init() {
        if(!databaseExists()) {
            if (new File("./database").mkdirs())
                System.out.println("Cartella database creata correttamente\n");  // crea la cartella se non esiste
            runSchema(schemaPath);
        }
    }

    @Override
    public void reset() { }

    /**
     * Restituisce una connessione al database.
     * @return Connessione al database
     * @throws SQLException se si verifica un errore nella connessione
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Controlla se il database esiste.
     * @return true se esiste, false altrimenti
     */
    private boolean databaseExists() {
        return new File("./database/" + dbFileName + ".mv.db").exists();
    }

    /**
     * Esegue lo schema del database.
     * @param resourcePath percorso del file SQL
     */
    public void runSchema(String resourcePath) {
        String sql;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Schema non trovato: " + resourcePath);
            sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String query : sql.split(";")) {
                if (!query.trim().isEmpty()) stmt.addBatch(query.concat(";"));
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}