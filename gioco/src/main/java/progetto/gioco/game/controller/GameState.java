package progetto.gioco.game.controller;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    private String attoCorrente;

    private Set<String> flags = new HashSet<>();
    private Set<String> npcInteragiti = new HashSet<>();
    private Set<String> scelteFatte = new HashSet<>();

    /** 
     * @param flag
     */
    public void addFlag(String flag) {
        flags.add(flag);
    }

    /** 
     * @param flag
     * @return boolean
     */
    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    /** 
     * @param sceltaId
     */
    public void addScelta(String sceltaId) {
        scelteFatte.add(sceltaId);
    }

    /** 
     * @param npcId
     */
    public void addNpcInteragito(String npcId) {
        npcInteragiti.add(npcId);
    }

    /** 
     * @param atto
     */
    public void setAttoCorrente(String atto) {
        this.attoCorrente = atto;
    }
}