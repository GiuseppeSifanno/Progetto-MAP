package engine.database;

import java.sql.SQLException;
import java.util.List;

public interface SalvataggioDAO<T> {

    void salva(T stato, int idSlot) throws SQLException;

    T carica(int idSlot) throws SQLException;

    List<Integer> listaSlotDisponibili();

    void elimina(int idSlot);
}