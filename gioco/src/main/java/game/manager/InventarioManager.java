package game.manager;

import java.util.ArrayList;
import java.util.List;

import engine.manager.BaseInventarioManager;
import engine.model.BaseOggetto;
import engine.observer.GameObservable;
import engine.observer.GameObserver;
import engine.observer.GameEvent;
import engine.observer.TipoEvento;
import game.database.MaterialeDAO;
import game.database.OggettoDAO;
import game.database.RicettaDAO;
import game.model.Inventario;
import game.model.Ricetta;

/**
 * Classe che gestisce l'inventario del giocatore.
 */
public class InventarioManager extends BaseInventarioManager implements GameObservable {
    private final List<GameObserver> observers;
    private final Inventario inventario;
    //TODO considerare una classe adeguata per le ricette/crafting
    private final List<Ricetta> ricette;

    private final OggettoDAO oggettoDAO;
    private final MaterialeDAO materialeDAO;
    private final RicettaDAO ricettaDAO;

    /**
     * Costruttore dell'inventario.
     * @param oggettoDAO oggettoDAO
     * @param materialeDAO materialeDAO
     * @param ricettaDAO ricettaDAO
     */
    public InventarioManager(OggettoDAO oggettoDAO, MaterialeDAO materialeDAO, RicettaDAO ricettaDAO) {
        this.observers = new ArrayList<>();
        this.inventario = new Inventario();
        this.ricette = new ArrayList<>();
        this.oggettoDAO = oggettoDAO;
        this.materialeDAO = materialeDAO;
        this.ricettaDAO = ricettaDAO;
    }

    /**
     * Aggiunge un oggetto all'inventario.
     * @param oggetto Oggetto da aggiungere all'inventario
     */
    @Override
    public void aggiungiOggetto(BaseOggetto oggetto) {
        inventario.aggiungi(oggetto);
        GameEvent event = new GameEvent(TipoEvento.OGGETTO_AGGIUNTO, oggetto);
        notifyObservers(event);
    }

    /**
     * Aggiunge un oggetto all'inventario in base all'id.
     * @param id id dell'oggetto da aggiungere
     */
    @Override
    public void aggiungiOggettoDaId(String id) {
        BaseOggetto oggetto = oggettoDAO.findById(id);
        if (oggetto == null) {
            oggetto = materialeDAO.findById(id);
        }
        if (oggetto == null) {
            throw new IllegalArgumentException("Oggetto non trovato: " + id);
        }
        aggiungiOggetto(oggetto);
    }

    /**
     * Rimuove un oggetto dall'inventario.
     * @param id Id dell'oggetto da rimuovere
     */
    @Override
    public void rimuoviOggetto(String id) {
        BaseOggetto oggetto = inventario.getOggetto(id);
        inventario.rimuovi(id);
        GameEvent event = new GameEvent(TipoEvento.OGGETTO_RIMOSSO, oggetto);
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
    public BaseOggetto combina(String id1, String id2) {
        for (Ricetta ricetta : ricette) {
            if (ricetta.matches(id1, id2)) {
                return oggettoDAO.findById(ricetta.idRisultato());
            }
        }
        return null;
    }

    @Override
    public void init() {
        // Oggetti/materiali caricati on-demand tramite oggettoDAO/materialeDAO
        ricette.addAll(ricettaDAO.findAll());
    }

    @Override
    public void reset() {
        inventario.oggetti().clear();
        ricette.clear();
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
