package engine.model;

import java.util.List;

public abstract class BaseInterazione extends BaseEntity {
    protected final List<String> condizioni;
    protected final String messaggioBloccato;
    protected final String messaggioSbloccato;

    public BaseInterazione(String id, List<String> condizioni,
                           String messaggioBloccato, String messaggioSbloccato) {
        super(id);
        this.condizioni = condizioni;
        this.messaggioBloccato = messaggioBloccato;
        this.messaggioSbloccato = messaggioSbloccato;
    }

    public List<String> getCondizioni() { return condizioni; }
    public String getMessaggioBloccato() { return messaggioBloccato; }
    public String getMessaggioSbloccato() { return messaggioSbloccato; }
}