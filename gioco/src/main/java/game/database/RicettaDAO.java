package game.database;

import java.util.List;
import engine.database.DAO;
import game.model.Ricetta;

public class RicettaDAO implements DAO<Ricetta, String> {

    @Override
    public Ricetta findById(String id) {
        return null;
    }

    @Override
    public List<Ricetta> findAll() {
        return null;
    }

    @Override
    public void save(Ricetta entity) {

    }

    @Override
    public void delete(String id) {

    }
}