package progetto.gioco.engine.database;

import java.util.List;

public interface SalvataggioDAO<T> {

    void salva(T stato, int idSlot);

    T carica(int idSlot);

    List<Integer> listaSlotDisponibili();

    void elimina(int idSlot);
}