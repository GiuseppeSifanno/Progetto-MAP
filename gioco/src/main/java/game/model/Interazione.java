package game.model;

import engine.model.BaseInterazione;
import engine.observer.Effetto;

import java.util.List;

public class Interazione extends BaseInterazione {
    private final List<Effetto> effetti;

    public Interazione(String id, List<String> condizioni,
                       String messaggioBloccato, String messaggioSbloccato,
                       List<Effetto> effetti) {
        super(id, condizioni, messaggioBloccato, messaggioSbloccato);
        this.effetti = effetti;
    }

    public List<Effetto> getEffetti() { return effetti; }
}