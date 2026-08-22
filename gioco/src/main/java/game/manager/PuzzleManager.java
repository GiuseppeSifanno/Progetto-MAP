package game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.manager.BasePuzzleManager;
import engine.observer.GameObservable;
import engine.observer.GameObserver;
import engine.observer.GameEvent;
import engine.observer.TipoEvento;
import game.database.PuzzleDAO;
import game.model.Puzzle;

public class PuzzleManager extends BasePuzzleManager implements GameObservable {
    private final List<GameObserver> observers;
    private final Map<String, Puzzle> mapPuzzles;
    private Puzzle puzzleCorrente;

    PuzzleDAO puzzleDAO;

    public PuzzleManager(PuzzleDAO puzzleDAO) {
        this.observers = new ArrayList<>();
        this.mapPuzzles = new HashMap<>();
        this.puzzles = new HashMap<>();
        this.puzzleDAO = puzzleDAO;
    }

    @Override
    public void aggiungiPuzzle(Puzzle puzzle) {
        mapPuzzles.put(puzzle.getId(), puzzle);
        puzzles.put(puzzle.getId(), puzzle);
    }

    @Override
    public Puzzle caricaPuzzle(String id) {
        this.puzzleCorrente = mapPuzzles.get(id);
        return puzzleCorrente;
    }


    @Override
    public boolean tentaRisoluzione(String input) {
        if (puzzleCorrente == null) {
            return false;
        }

        boolean risolto = puzzleCorrente.risolvi(input);

        if (risolto) {
            GameEvent event = new GameEvent();
            event.setTipo(TipoEvento.PUZZLE_RISOLTO);
            event.setPayload(puzzleCorrente.getId());
            notifyObservers(event);
        }

        return risolto;
    }

    /** 
     * @param id id puzzle da verificare
     * @return boolean
     */
    public boolean isPuzzleRisolto(String id) {
        Puzzle puzzle = mapPuzzles.get(id);
        return puzzle != null && puzzle.isRisolto();
    }

    @Override
    public void init() {
        // Carica tutti i puzzle da file
    }

    @Override
    public void reset() {
        puzzleCorrente = null;
    }

    /** 
     * @param observer observer da aggiungere
     */
    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** 
     * @param observer observer da rimuovere
     */
    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(GameObserver observer, GameEvent evento) {

    }

    /** 
     * @param event evento da notificare
     */
    private void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}
