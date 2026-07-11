package game.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import engine.database.DAO;
import engine.database.DBManager;
import game.model.oggetti.Materiale;
import game.model.oggetti.Oggetto;

public class MaterialeDAO extends BaseDAO<Materiale> implements DAO<Materiale, String> {
    public MaterialeDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    protected Materiale mapRow(ResultSet rs) throws SQLException {
        return new Materiale(
                rs.getString("id_oggetto"),
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
        return List.of();
    }
    @Override
    public void save(Materiale entity) {
    }
    @Override
    public void delete(String s) {
    }
}