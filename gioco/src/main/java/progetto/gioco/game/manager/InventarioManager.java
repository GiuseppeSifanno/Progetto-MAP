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

/**
 * Classe che gestisce l'inventario del giocatore.
 */
public class InventarioManager extends BaseInventarioManager implements GameObservable {
    private final List<GameObserver> observers;
    private final Inventario inventario;
    private final List<Ricetta> ricette;

    /**
     * Inizializza con un inventario già esistente.
     * @param inventario inventario esistente
     */
    public InventarioManager(Inventario inventario) {
        this.observers = new ArrayList<>();
        this.inventario = inventario;
        this.ricette = new ArrayList<>();
        this.oggetti = inventario.getOggetti();
    }

    /**
     * Inizializza un nuovo inventario vuoto.
     */
    public InventarioManager() {
        this.observers = new ArrayList<>();
        this.inventario = new Inventario();
        this.ricette = new ArrayList<>();
    }

    /** 
     * @param ricetta Ricetta del crafting
     */
    public void addRicetta(Ricetta ricetta) {
        this.ricette.add(ricetta);
    }

    @Override
    public List<BaseOggetto> getOggetti() {
        return inventario.getOggetti();
    }

    /**
     * Aggiunge un oggetto all'inventario.
     * @param oggetto Oggetto da aggiungere all'inventario
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
     * Rimuove un oggetto dall'inventario.
     * @param id Id dell'oggetto da rimuovere
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
     * Controlla se l'inventario contiene un oggetto specifico.
     * @param id Id dell'oggetto da cercare
     * @return boolean
     */
    @Override
    public boolean hasOggetto(String id) {
        return inventario.hasOggetto(id);
    }

    /**
     * Costruisce un oggetto utilizzando due ingredienti
     * @param id1 id del primo oggetto
     * @param id2 id del secondo oggetto
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

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }


    @Override
    public void notifyObserver(GameObserver observer, GameEvent event) {
        observer.onEvent(event);
    }

    /**
     * Notifica a <b>tutti</b> gli observer registrati un evento.
     * @param event Evento da notificare agli observer
     */
    private void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}
