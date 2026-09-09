package game.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import engine.database.DAO;
import engine.database.DBManager;
import game.model.Ricetta;

/**
 * DAO per le ricette di combinazione. Una ricetta ha un numero variabile di
 * ingredienti, quindi lo schema usa due tabelle:
 * - RICETTA(ID_RICETTA, ID_RISULTATO)
 * - RICETTA_INGREDIENTE(ID_RICETTA, ID_INGREDIENTE)  — una riga per ingrediente
 */
public class RicettaDAO extends BaseDAO<Ricetta> implements DAO<Ricetta, String> {
    public RicettaDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    protected Ricetta mapRow(ResultSet rs) throws SQLException {
        // Non utilizzabile qui: gli ingredienti richiedono una query separata
        // (vedi findById/findAll). Manteniamo il metodo per rispettare il
        // contratto di BaseDAO, come già fatto in StatoGiocoDAO.
        throw new SQLException(new UnsupportedOperationException("Usare findById/findAll"));
    }

    @Override
    public Ricetta findById(String id) {
        String sqlRicetta = "SELECT ID_RISULTATO FROM PUBLIC.RICETTA WHERE ID_RICETTA = ?";

        try (Connection conn = dbManager.getConnection()) {
            String idRisultato;
            try (PreparedStatement stmt = conn.prepareStatement(sqlRicetta)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) return null;
                    idRisultato = rs.getString("ID_RISULTATO");
                }
            }

            List<String> ingredienti = caricaIngredienti(conn, id);
            return new Ricetta(id, ingredienti, idRisultato);

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query: " + e.getMessage() + "\nid: " + id, e);
        }
    }

    @Override
    public List<Ricetta> findAll() {
        String sql = "SELECT ID_RICETTA, ID_RISULTATO FROM PUBLIC.RICETTA";
        List<Ricetta> ricette = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<String[]> righe = new ArrayList<>(); // [idRicetta, idRisultato]
            while (rs.next()) {
                righe.add(new String[]{rs.getString("ID_RICETTA"), rs.getString("ID_RISULTATO")});
            }

            for (String[] riga : righe) {
                List<String> ingredienti = caricaIngredienti(conn, riga[0]);
                ricette.add(new Ricetta(riga[0], ingredienti, riga[1]));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query: " + e.getMessage());
        }
        return ricette;
    }

    private List<String> caricaIngredienti(Connection conn, String idRicetta) throws SQLException {
        List<String> ingredienti = new ArrayList<>();
        String sql = "SELECT ID_INGREDIENTE FROM PUBLIC.RICETTA_INGREDIENTE WHERE ID_RICETTA = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idRicetta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredienti.add(rs.getString("ID_INGREDIENTE"));
                }
            }
        }
        return ingredienti;
    }

    @Override
    public void save(Ricetta entity) {
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sqlRicetta = "MERGE INTO RICETTA (ID_RICETTA, ID_RISULTATO) KEY (ID_RICETTA) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlRicetta)) {
                    stmt.setString(1, entity.getIdRicetta());
                    stmt.setString(2, entity.getIdRisultato());
                    stmt.executeUpdate();
                }

                try (PreparedStatement del = conn.prepareStatement(
                        "DELETE FROM RICETTA_INGREDIENTE WHERE ID_RICETTA = ?")) {
                    del.setString(1, entity.getIdRicetta());
                    del.executeUpdate();
                }

                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO RICETTA_INGREDIENTE (ID_RICETTA, ID_INGREDIENTE) VALUES (?, ?)")) {
                    for (String idIngrediente : entity.getIngredienti()) {
                        ins.setString(1, entity.getIdRicetta());
                        ins.setString(2, idIngrediente);
                        ins.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio della ricetta", e);
        }
    }

    @Override
    public void delete(String id) {
        try (Connection conn = dbManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM RICETTA_INGREDIENTE WHERE ID_RICETTA = ?")) {
                del.setString(1, id);
                del.executeUpdate();
            }
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PUBLIC.RICETTA WHERE ID_RICETTA = ?")) {
                del.setString(1, id);
                del.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione della ricetta con id " + id, e);
        }
    }
}