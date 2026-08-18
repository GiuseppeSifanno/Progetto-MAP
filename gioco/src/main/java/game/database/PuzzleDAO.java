package game.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import engine.database.DAO;
import engine.database.DBManager;
import game.model.Puzzle;

public class PuzzleDAO extends BaseDAO<Puzzle> implements DAO<Puzzle, String> {

    public PuzzleDAO(DBManager dbManager) {
        super(dbManager);
    }

    @Override
    public Puzzle findById(String id) {
        return null;
    }

    @Override
    public List<Puzzle> findAll() {
        return null;
    }

    @Override
    public void save(Puzzle entity) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    protected Puzzle mapRow(ResultSet rs) throws SQLException {
        return null;
    }
}