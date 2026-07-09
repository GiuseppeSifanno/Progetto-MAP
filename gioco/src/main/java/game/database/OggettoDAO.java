package game.database;

import java.util.List;
import engine.database.DAO;
import game.model.oggetti.Oggetto;

public class OggettoDAO implements DAO<Oggetto, String> {

    @Override
    public Oggetto findById(String id) {
        return null;
    }

    @Override
    public List<Oggetto> findAll() {
        return null;
    }

    @Override
    public void save(Oggetto entity) {

    }

    @Override
    public void delete(String id) {

    }
}