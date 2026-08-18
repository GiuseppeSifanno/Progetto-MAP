package game.database;

import engine.database.DAO;
import engine.database.DBManager;
import game.model.oggetti.Oggetto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OggettoDAO extends BaseDAO<Oggetto> implements DAO<Oggetto, String> {
    public OggettoDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    protected Oggetto mapRow(ResultSet rs) throws SQLException {
        return new Oggetto(
                rs.getString("id_oggetto"),
                rs.getString("nome"),
                rs.getString("descrizione"),
                rs.getString("image_name")
        );
    }

    @Override
    public Oggetto findById(String id) {
        String sql = "SELECT * FROM PUBLIC.OGGETTO WHERE OGGETTO.ID_OGGETTO = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next())
                    return mapRow(rs);
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Errore durante la query: " + e.getMessage() + "\nid: " + id, e);
        }

        return null;
    }

    @Override
    public List<Oggetto> findAll() {
        String sql = "SELECT * FROM PUBLIC.OGGETTO";
        List<Oggetto> oggetti = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                oggetti.add(mapRow(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore durante la query: " + e.getMessage());
        }
        return oggetti;
    }

    @Override
    public void save(Oggetto entity) {
        // MERGE è SPECIFICO per H2
        String sql = """
        MERGE INTO Oggetto (id_oggetto, nome, descrizione, image_name, combinabile)
        KEY (id_oggetto)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getId());
            stmt.setString(2, entity.getNome());
            stmt.setString(3, entity.getDescrizione());
            stmt.setString(4, entity.getFilename());
            stmt.setBoolean(5, false);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dell'oggetto", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Oggetto WHERE id_oggetto = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione dell'oggetto con id " + id, e);
        }
    }
}
