package game.model;

import engine.model.BaseZona;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe che rappresenta una zona del gioco.
 */
public class Zona extends BaseZona<Interazione> {

    private final Map<String, Movimento> movimenti;

    /**
     * Costruttore della classe Zona.
     * @param idZona id univoco della zona
     * @param interazioni interazioni disponibili nella zona
     */
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