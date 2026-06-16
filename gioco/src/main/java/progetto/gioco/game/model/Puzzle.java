package progetto.gioco.game.model;

import progetto.gioco.engine.model.BasePuzzle;

public class Puzzle extends BasePuzzle {
    private String soluzione;

    public Puzzle(String id, String soluzione) {
        super(id, false);
        this.soluzione = soluzione;
    }

    public Puzzle(String id, String soluzione, boolean risolto) {
        super(id, risolto);
        this.soluzione = soluzione;
    }

    /**
     * @return String
     */
    public String getSoluzione() {
        return soluzione;
    }

    /**
     * @param input
     * @return boolean
     */
    @Override
    public boolean risolvi(String input) {
        if (risolto) {
            return true;
        }

        if (input != null && input.equalsIgnoreCase(soluzione)) {
            this.risolto = true;
            return true;
        }
        return false;
    }
}
