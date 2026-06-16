package progetto.gioco.game.model;

import java.util.ArrayList;
import java.util.List;

import progetto.gioco.engine.model.BaseDialogo;

public class Dialogo extends BaseDialogo{
    private List<Scelta> scelte;

    public Dialogo(String id, String idDialogo, String testo, List<Scelta> scelte) {
        super(id, idDialogo, testo);
        this.scelte = scelte;
    }

    public Dialogo(String id, String idDialogo, String testo) {
        super(id, idDialogo, testo);
        this.scelte = new ArrayList<>();
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
}
