package progetto.gioco.engine.manager;

import java.util.List;

import progetto.gioco.engine.model.BaseOggetto;

/**
 * Classe astratta che gestisce l'inventario.
 */
public abstract class BaseInventarioManager extends BaseManager{
    /**
     * Lista degli oggetti presenti nell'inventario.
     */
    protected List<BaseOggetto> oggetti;

    /**
     * Aggiunge un oggetto all'inventario.
     * @param oggetto ogetto da aggiungere
     */
    public abstract void aggiungiOggetto(BaseOggetto oggetto);

    /**
     * Rimuove un oggetto dall'inventario.
     * @param id id dell'oggetto da rimuovere
     */
    public abstract void rimuoviOggetto(String id);

    /**
     * Controlla se l'inventario contiene un oggetto.
     * @param id id dell'oggetto da controllare
     * @return true se l'inventario contiene l'oggetto, false altrimenti
     */
    public abstract boolean hasOggetto(String id);
}
