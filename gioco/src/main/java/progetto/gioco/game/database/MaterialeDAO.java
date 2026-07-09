package progetto.gioco.game.database;

import java.util.List;
import progetto.gioco.engine.database.DAO;
import progetto.gioco.game.model.oggetti.Materiale;

public class MaterialeDAO implements DAO<Materiale, String> {

    @Override
    public Materiale findById(String id) {
        return null;
    }

    @Override
    public List<Materiale> findAll() {
        return null;
    }

    @Override
    public void save(Materiale entity) {

    }

    @Override
    public void delete(String id) {

    }
}