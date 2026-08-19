package game.model;

import engine.model.BaseZona;

import java.util.Map;

public class Zona extends BaseZona<Interazione> {
    public Zona(String idZona, Map<String, Interazione> interazioni) {
        super(idZona, interazioni);
    }
}