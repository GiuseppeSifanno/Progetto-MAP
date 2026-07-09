package progetto.gioco.game.database;

import java.util.List;
import progetto.gioco.engine.database.DAO;
import progetto.gioco.game.model.oggetti.Oggetto;

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