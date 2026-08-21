package game.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import engine.database.DAO;
import engine.database.DBManager;
import game.model.oggetti.Materiale;

public class MaterialeDAO extends BaseDAO<Materiale> implements DAO<Materiale, String> {
    public MaterialeDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    protected Materiale mapRow(ResultSet rs) throws SQLException {
        return new Materiale(
                rs.getString("id_materiale"),
                rs.getString("nome"),
                rs.getString("descrizione"),
                rs.getString("image_name")
        );
    }

    @Override
    public Materiale findById(String id) {
        String sql = "SELECT * FROM PUBLIC.MATERIALE WHERE MATERIALE.ID_MATERIALE = ?";
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
    public List<Materiale> findAll() {
        String sql = "SELECT * FROM PUBLIC.MATERIALE";
        List<Materiale> materiali = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                materiali.add(mapRow(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Errore durante la query: " + e.getMessage());
        }
        return materiali;
    }
    @Override
    public void save(Materiale entity) {
        // MERGE è SPECIFICO per H2
        String sql = """
        MERGE INTO MATERIALE (ID_MATERIALE, nome, descrizione, image_name)
        KEY (ID_MATERIALE)
        VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getId());
            stmt.setString(2, entity.getNome());
            stmt.setString(3, entity.getDescrizione());
            stmt.setString(4, entity.getFilename());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio dell'oggetto", e);
        }
    }
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM MATERIALE WHERE ID_MATERIALE = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'eliminazione dell'oggetto con id " + id, e);
        }
    }
}