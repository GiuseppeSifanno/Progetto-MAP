package game.model;

import java.util.ArrayList;
import java.util.List;

import engine.model.BaseDialogo;
import engine.model.Battuta;

public class Dialogo extends BaseDialogo {
    private final List<Scelta> scelte;
    private String nextId;

    public Dialogo(String idDialogo, List<Battuta> battute, List<Scelta> scelte) {
        super(idDialogo, battute);
        this.scelte = scelte;
        this.nextId = null;
    }

    public Dialogo(String idDialogo, List<Battuta> battute, List<Scelta> scelte, String nextId) {
        super(idDialogo, battute);
        this.scelte = scelte;
        this.nextId = nextId;
    }

    public Dialogo(String idDialogo, List<Battuta> battute) {
        super(idDialogo, battute);
        this.scelte = new ArrayList<>();
        this.nextId = null;
    }

    public Dialogo(String idDialogo, List<Battuta> battute, String nextId) {
        super(idDialogo, battute);
        this.scelte = new ArrayList<>();
        this.nextId = nextId;
    }

    public List<Scelta> getScelte() {
        return scelte;
    }

    public int getNumeroScelte() {
        return scelte.size();
    }

    @Override
    public List<Battuta> getBattute() {
        return this.battute;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
}