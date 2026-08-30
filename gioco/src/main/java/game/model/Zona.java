package game.model;

import engine.model.BaseZona;

import java.util.HashMap;
import java.util.Map;

public class Zona extends BaseZona<Interazione> {

    private final Map<String, Movimento> movimenti;

    public Zona(String idZona, Map<String, Interazione> interazioni) {
        super(idZona, interazioni);
        this.movimenti = new HashMap<>();
    }

    public void aggiungiMovimento(String direzione, Movimento movimento) {
        movimenti.put(direzione, movimento);
    }

    public Movimento getMovimento(String direzione) {
        return movimenti.get(direzione);
    }

    public Map<String, Movimento> getMovimenti() {
        return Map.copyOf(movimenti);
    }
}