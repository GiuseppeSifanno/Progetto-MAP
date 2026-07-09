package game.model;

import java.util.ArrayList;
import java.util.List;

import engine.model.BaseDialogo;

public class Dialogo extends BaseDialogo{
    private final List<Scelta> scelte;
    private String nextId;  // Dialogo successivo se terminato

    public Dialogo(String idDialogo, String testo, List<Scelta> scelte) {
        super(idDialogo, testo);
        this.scelte = scelte;
        this.nextId = null;
    }

    public Dialogo(String idDialogo, String testo, List<Scelta> scelte, String nextId) {
        super(idDialogo, testo);
        this.scelte = scelte;
        this.nextId = nextId;
    }

    public Dialogo(String idDialogo, String testo) {
        super(idDialogo, testo);
        this.scelte = new ArrayList<>();
        this.nextId = null;
    }

    public Dialogo(String idDialogo, String testo, String nextId) {
        super(idDialogo, testo);
        this.scelte = new ArrayList<>();
        this.nextId = nextId;
    }

    /** 
     * @return List<Scelta>
     */
    public List<Scelta> getScelte() {
        return scelte;
    }

    /** 
     * @return int
     */
    public int getNumeroScelte(){
        return scelte.size();
    }

    /** 
     * @return String
     */
    public String getNextId() {
        return nextId;
    }

    /** 
     * @param nextId
     */
    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
}