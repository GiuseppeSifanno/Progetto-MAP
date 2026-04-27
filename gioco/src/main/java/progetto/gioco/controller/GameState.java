package progetto.gioco.controller;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    private String attoCorrente;

    private Set<String> flags = new HashSet<>();
    private Set<String> npcInteragiti = new HashSet<>();
    private Set<String> scelteFatte = new HashSet<>();

    public void addFlag(String flag) {
        flags.add(flag);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public void addScelta(String sceltaId) {
        scelteFatte.add(sceltaId);
    }

    public void addNpcInteragito(String npcId) {
        npcInteragiti.add(npcId);
    }

    public void setAttoCorrente(String atto) {
        this.attoCorrente = atto;
    }
}