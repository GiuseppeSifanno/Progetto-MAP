package game.model;

import engine.model.BaseOggetto;

public class Oggetto extends BaseOggetto {
    public Oggetto(String id, String nome, String descrizione, String filename, boolean combinabile) {
        super(id, nome, descrizione, filename, combinabile);
    }
}
