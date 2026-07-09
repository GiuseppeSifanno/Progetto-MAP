package progetto.gioco.game.manager;

import java.util.List;

import progetto.gioco.engine.manager.BaseSaveManager;
import progetto.gioco.game.database.StatoGiocoDAO;

public class SaveManager extends BaseSaveManager {
    private final StatoGiocoDAO statoGiocoDAO;

    public SaveManager(StatoGiocoDAO statoGiocoDAO) {
        this.statoGiocoDAO = statoGiocoDAO;
    }

    @Override
    public void salva(Object stato) {
        // TODO: cast a StatoGioco, delega a statoGiocoDAO.salva(...)
    }

    @Override
    public Object carica(int idSlot) {
        // TODO: delega a statoGiocoDAO.carica(idSlot)
        return null;
    }

    @Override
    public void elimina(int idSlot) {
        // TODO: delega a statoGiocoDAO.elimina(idSlot)
    }

    public List<Integer> listaSalvataggi() {
        // TODO: delega a statoGiocoDAO.listaSlotDisponibili()
        return null;
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
