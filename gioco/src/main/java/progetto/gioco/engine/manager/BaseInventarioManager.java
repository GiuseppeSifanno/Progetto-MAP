package progetto.gioco.engine.manager;

import java.util.List;

import progetto.gioco.engine.model.BaseOggetto;

public abstract class BaseInventarioManager extends BaseManager{
    protected List<BaseOggetto> oggetti;

    public abstract void aggiungiOggetto(BaseOggetto oggetto);
    
    public abstract void rimuoviOggetto(String id);

    public abstract boolean hasOggetto(String id);
}
