package progetto.gioco.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import progetto.gioco.engine.manager.BasePuzzleManager;
import progetto.gioco.engine.observer.GameObservable;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.model.Puzzle;

public class PuzzleManager extends BasePuzzleManager implements GameObservable {
    private final List<GameObserver> observers;
    private final Map<String, Puzzle> mapPuzzles;
    private Puzzle puzzleCorrente;

    public PuzzleManager() {
        this.observers = new ArrayList<>();
        this.mapPuzzles = new HashMap<>();
        this.puzzles = new HashMap<>();
    }

    /** 
     * @param puzzle
     */
    public void addPuzzle(Puzzle puzzle) {
        mapPuzzles.put(puzzle.getId(), puzzle);
        puzzles.put(puzzle.getId(), puzzle);
    }

    /**
     * @param id
     */
    @Override
    public void caricaPuzzle(String id) {
        this.puzzleCorrente = mapPuzzles.get(id);
    }

    /**
     * @param input
     * @return boolean
     */
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
     * @param id
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

    @Override
    public void notifyObserver(GameObserver observer, GameEvent evento) {

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
