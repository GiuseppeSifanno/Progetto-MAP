package game.manager;

import java.sql.SQLException;
import java.util.List;

import engine.manager.BaseSaveManager;
import game.database.StatoGiocoDAO;
import game.model.StatoGioco;

public class SaveManager extends BaseSaveManager {
    private final StatoGiocoDAO statoGiocoDAO;

    public SaveManager(StatoGiocoDAO statoGiocoDAO) {
        this.statoGiocoDAO = statoGiocoDAO;
    }

    @Override
    public void salva(Object stato, int idSlot) throws SQLException {
        statoGiocoDAO.salva((StatoGioco) stato, idSlot);
    }

    @Override
    public Object carica(int idSlot) throws SQLException {
        return statoGiocoDAO.carica(idSlot);
    }

    @Override
    public void elimina(int idSlot) {
        statoGiocoDAO.elimina(idSlot);
    }

    public List<Integer> listaSalvataggi() {
        return statoGiocoDAO.listaSlotDisponibili();
    }

    @Override
    public void init() {
        // TODO: eventuale verifica connessione DB
    }

    @Override
    public void reset() {
        // TODO: da definire cosa significa "reset" per un DAO-based manager
    }
}
