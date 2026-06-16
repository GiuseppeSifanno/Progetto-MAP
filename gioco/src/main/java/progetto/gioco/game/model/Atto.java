package progetto.gioco.game.model;

import java.util.Map;

import progetto.gioco.engine.model.BaseAtto;

public class Atto extends BaseAtto {
    public Atto(String idAtto, Map<String, Dialogo> dialoghi, String dialogoIniziale) {
        super(idAtto, dialoghi, dialogoIniziale);
    }
}
