package engine.manager;

import java.util.Map;

import engine.model.BasePuzzle;

/**
 * Classe astratta che gestisce i puzzle.
 */
public abstract class BasePuzzleManager extends BaseManager{
    /**
     * Map che contiene tutti i puzzle.
     */
    protected Map<String, BasePuzzle> puzzles;

    /**
     * Carica un puzzle dato un i
     * @param id id del puzzle
     */
    public abstract void caricaPuzzle(String id);

    /**
     * Tenta di risolvere il puzzle dato un input.
     * @param input input da risolvere
     * @return true se risolto, false altrimenti
     */
    public abstract boolean tentaRisoluzione(String input);
}
