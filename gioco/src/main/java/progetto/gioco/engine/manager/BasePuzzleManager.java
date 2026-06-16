package progetto.gioco.engine.manager;

import java.util.Map;

import progetto.gioco.engine.model.BasePuzzle;

public abstract class BasePuzzleManager extends BaseManager{
    protected Map<String, BasePuzzle> puzzles;
    
    public abstract void caricaPuzzle(String id);
    
    public abstract boolean tentaRisoluzione(String input);
}
