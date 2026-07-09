package progetto.gioco.engine.database;

import progetto.gioco.engine.manager.BaseManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBManager extends BaseManager {
    private final String url;
    private final String user;
    private final String pass;
    private final String schemaPath;
    private final String dbFileName;

    public DBManager(String configPath) {
        Properties props = loadConfig(configPath);
        this.dbFileName = props.getProperty("db.name", "game");
        this.schemaPath = props.getProperty("db.schema", "db/schema.sql");
        this.user = props.getProperty("db.user", "sa");
        this.pass = props.getProperty("db.pass", "");
        this.url = "jdbc:h2:file:./database/" + dbFileName;
    }

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
        if (!databaseExists()) runSchema(schemaPath);
    }

    @Override
    public void reset() { }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private boolean databaseExists() {
        return new File("./database/" + dbFileName + ".mv.db").exists();
    }

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
                if (!query.trim().isEmpty()) stmt.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}