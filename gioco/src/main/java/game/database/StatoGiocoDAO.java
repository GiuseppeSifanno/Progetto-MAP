package game.database;

import java.util.List;
import engine.database.SalvataggioDAO;
import game.model.StatoGioco;

public class StatoGiocoDAO implements SalvataggioDAO<StatoGioco> {

    @Override
    public void salva(StatoGioco stato, int idSlot) {

    }

    @Override
    public StatoGioco carica(int idSlot) {
        return null;
    }

    @Override
    public List<Integer> listaSlotDisponibili() {
        return null;
    }

    @Override
    public void elimina(int idSlot) {

    }
}