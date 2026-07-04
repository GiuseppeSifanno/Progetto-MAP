package progetto.gioco.game.manager;

import java.util.ArrayList;
import java.util.List;

import progetto.gioco.engine.manager.BaseInventarioManager;
import progetto.gioco.engine.model.BaseOggetto;
import progetto.gioco.engine.observer.GameObservable;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.model.Inventario;
import progetto.gioco.game.model.Ricetta;

public class InventarioManager extends BaseInventarioManager implements GameObservable {
    private List<GameObserver> observers;
    private Inventario inventario;
    private List<Ricetta> ricette;

    public InventarioManager(Inventario inventario) {
        this.observers = new ArrayList<>();
        this.inventario = inventario;
        this.ricette = new ArrayList<>();
        this.oggetti = inventario.getOggetti();
    }

    public InventarioManager() {
        this.observers = new ArrayList<>();
        this.inventario = new Inventario();
        this.ricette = new ArrayList<>();
    }

    /** 
     * @param ricetta
     */
    public void addRicetta(Ricetta ricetta) {
        this.ricette.add(ricetta);
    }

    @Override
    public List<BaseOggetto> getOggetti() {
        return inventario.getOggetti();
    }

    /**
     * @param oggetto
     */
    @Override
    public void aggiungiOggetto(BaseOggetto oggetto) {
        inventario.aggiungi(oggetto);
        GameEvent event = new GameEvent();
        event.setTipo(TipoEvento.OGGETTO_AGGIUNTO);
        event.setPayload(oggetto);
        notifyObservers(event);
    }

    /**
     * @param id
     */
    @Override
    public void rimuoviOggetto(String id) {
        BaseOggetto oggetto = inventario.getOggetto(id);
        inventario.rimuovi(id);
        GameEvent event = new GameEvent();
        event.setTipo(TipoEvento.OGGETTO_RIMOSSO);
        event.setPayload(oggetto);
        notifyObservers(event);
    }

    /**
     * @param id
     * @return boolean
     */
    @Override
    public boolean hasOggetto(String id) {
        return inventario.hasOggetto(id);
    }

    /** 
     * @param id1
     * @param id2
     * @return BaseOggetto
     */
    public BaseOggetto craft(String id1, String id2) {
        for (Ricetta ricetta : ricette) {
            if (ricetta.matches(id1, id2)) {
                // Simula la creazione dell'oggetto
                return new progetto.gioco.game.model.oggetti.Oggetto(
                        ricetta.getIdRisultato(),
                        "Oggetto Craftato",
                        "Descrizione",
                        "filename"
                );
            }
        }
        return null;
    }

    @Override
    public void init() {
        // Inventario già creato nel costruttore
        // Potrebbe caricare oggetti iniziali da file JSON o Database
    }

    @Override
    public void reset() {
        inventario.getOggetti().clear();
    }

    /** 
     * @param observer
     */
    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** 
     * @param observer
     */
    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    /** 
     * @param observer
     */
    @Override
    public void notifyObserver(GameObserver observer) {
    }

    /** 
     * @param event
     */
    private void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}
