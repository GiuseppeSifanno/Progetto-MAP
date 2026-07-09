package game.model;

import java.util.Map;

import engine.model.BaseAtto;

public class Atto extends BaseAtto<Dialogo> {
    public Atto(String idAtto, Map<String, Dialogo> dialoghi, String dialogoIniziale) {
        super(idAtto, dialoghi, dialogoIniziale);
    }
}
