package engine.model;

import java.util.Map;

public abstract class BaseZona<I extends BaseInterazione> {
    protected final String idZona;
    protected final Map<String, I> interazioni;
    public BaseZona(String idZona, Map<String, I> interazioni) {
        this.idZona = idZona;
        this.interazioni = interazioni;
    }
    public String getIdZona() { return idZona; }
    public Map<String, I> getInterazioni() { return interazioni; }
}
