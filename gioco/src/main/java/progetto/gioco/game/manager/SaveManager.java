package progetto.gioco.game.manager;

import java.util.ArrayList;
import java.util.List;

import progetto.gioco.engine.manager.BaseSaveManager;
import progetto.gioco.game.model.StatoGioco;

public class SaveManager extends BaseSaveManager {
    private List<StatoGioco> salvataggi;

    public SaveManager() {
        this.salvataggi = new ArrayList<>();
    }

    /**
     * @param stato
     */
    @Override
    public void salva(Object stato) {
        if (stato instanceof StatoGioco) {
            salvataggi.add((StatoGioco) stato);
        }
    }

    /**
     * @param idSlot
     * @return Object
     */
    @Override
    public Object carica(int idSlot) {
        if (idSlot >= 0 && idSlot < salvataggi.size()) {
            return salvataggi.get(idSlot);
        }
        return null;
    }

    /**
     * @param idSlot
     */
    @Override
    public void elimina(int idSlot) {
        if (idSlot >= 0 && idSlot < salvataggi.size()) {
            salvataggi.remove(idSlot);
        }
    }

    /** 
     * @return List<StatoGioco>
     */
    public List<StatoGioco> listaSalvataggi() {
        return new ArrayList<>(salvataggi);
    }

    @Override
    public void init() {
    }

    @Override
    public void reset() {
        salvataggi.clear();
    }
}
