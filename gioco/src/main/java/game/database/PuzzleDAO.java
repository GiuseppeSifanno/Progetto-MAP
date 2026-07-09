package game.database;

import java.util.List;
import engine.database.DAO;
import game.model.Puzzle;

public class PuzzleDAO implements DAO<Puzzle, String> {

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
}