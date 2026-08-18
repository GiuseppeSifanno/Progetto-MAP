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

public class RicettaDAO extends BaseDAO<Ricetta> implements DAO<Ricetta, String> {
    public RicettaDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    protected Ricetta mapRow(ResultSet rs) throws SQLException {
        return new Ricetta(
                rs.getString("id_ricetta"),
                rs.getString("id_ingrediente1"),
                rs.getString("id_ingrediente2"),
                rs.getString("id_risultato")
        );
    }

    @Override
    public Ricetta findById(String id) {
        String sql = "SELECT * FROM PUBLIC.RICETTA WHERE RICETTA.ID_RICETTA = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la query: " + e.getMessage() + "\nid: " + id, e);
        }
        return null;
    }

    @Override
    public List<Ricetta> findAll() {
        String sql = "SELECT * FROM PUBLIC.RICETTA";
        List<Ricetta> ricette = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                ricette.add(mapRow(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore durante la query: " + e.getMessage());
        }
        return ricette;
    }

    @Override
    public void save(Ricetta entity) {
        // MERGE è SPECIFICO per H2
        String sql = """
        MERGE INTO RICETTA (ID_RICETTA, ID_INGREDIENTE1, ID_INGREDIENTE2, ID_RISULTATO)\s
        KEY (ID_RICETTA)
        VALUES (?, ?, ?, ?)
       \s""";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getIdRicetta());
            stmt.setString(2, entity.getIdIngrediente1());
            stmt.setString(3, entity.getIdIngrediente2());
            stmt.setString(4, entity.getIdRisultato());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dell'oggetto", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM PUBLIC.RICETTA WHERE ID_RICETTA = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione dell'oggetto con id " + id, e);
        }
    }
}