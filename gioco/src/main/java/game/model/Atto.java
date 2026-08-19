package game.model;

import java.util.Map;

import engine.model.BaseAtto;
import engine.model.Personaggio;

public class Atto extends BaseAtto<Dialogo> {
    public Atto(String idAtto, String dialogoIniziale, Map<String, Personaggio> personaggi, Map<String, Dialogo> dialoghi) {
        super(idAtto, dialogoIniziale, personaggi, dialoghi);
    }
}
