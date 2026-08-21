package engine.model;

import java.util.List;

/** Classe astratta che rappresenta un'interazione. */
public abstract class BaseInterazione extends BaseEntity {
    /** Lista delle condizioni che devono essere soddisfatte per poter usare l'interazione. */
    protected final List<String> condizioni;
    /** Messaggi di blocco e sblocco dell'interazione. */
    protected final String messaggioBloccato;
    /** Messaggio di sblocco dell'interazione. */
    protected final String messaggioSbloccato;

    /**
     * Costruttore di base.
     * @param id Id interazione
     * @param condizioni Lista delle condizioni
     * @param messaggioBloccato Messaggio di blocco
     * @param messaggioSbloccato Messaggio di sblocco
     */
    public BaseInterazione(String id, List<String> condizioni,
                           String messaggioBloccato, String messaggioSbloccato) {
        super(id);
        this.condizioni = condizioni;
        this.messaggioBloccato = messaggioBloccato;
        this.messaggioSbloccato = messaggioSbloccato;
    }

    /**
     * Restituisce la lista delle condizioni.
     * @return List<String>
     */
    public List<String> getCondizioni() { return condizioni; }
    /**
     * Restituisce il messaggio di blocco.
     * @return String
     */
    public String getMessaggioBloccato() { return messaggioBloccato; }
    /**
     * Restituisce il messaggio di sblocco.
     * @return String
     */
    public String getMessaggioSbloccato() { return messaggioSbloccato; }
}