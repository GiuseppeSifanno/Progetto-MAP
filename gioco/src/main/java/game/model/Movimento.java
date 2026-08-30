package game.model;

public class Movimento {

    private final String direzione;
    private final String zonaDestinazione;

    public Movimento(String direzione, String zonaDestinazione) {
        this.direzione = direzione;
        this.zonaDestinazione = zonaDestinazione;
    }

    public String getDirezione() {
        return direzione;
    }

    public String getZonaDestinazione() {
        return zonaDestinazione;
    }
}
