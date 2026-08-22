package engine.manager;

import engine.model.BaseOggetto;
import engine.model.Inventario;

/**
 * Classe astratta che gestisce l'inventario.
 */
public abstract class BaseInventarioManager extends BaseManager{
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
     * Ripristina l'inventario a uno stato specifico.
     * @param salvato Inventario da ripristinare
     */
    public abstract void ripristina(Inventario salvato);

    /**
     * Controlla se l'inventario contiene un oggetto.
     * @param id id dell'oggetto da controllare
     * @return true se l'inventario contiene l'oggetto, false altrimenti
     */
    public abstract boolean hasOggetto(String id);

    /**
     * Aggiunge un oggetto all'inventario in base all'id.
     * @param id id dell'oggetto da aggiungere
     */
    public abstract void aggiungiOggettoDaId(String id);
}
