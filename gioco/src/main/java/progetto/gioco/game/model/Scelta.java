package progetto.gioco.game.model;

import progetto.gioco.engine.model.BaseScelta;

public class Scelta extends BaseScelta{
    public Scelta(String id, String idScelta, String testo, String next) {
        super(id, idScelta, testo, next);
    }

    public Scelta(String id, String idScelta, String testo) {
        super(id, idScelta, testo, null);
    }
}