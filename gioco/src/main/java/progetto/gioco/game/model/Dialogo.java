package progetto.gioco.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dialogo {
    private String idDialogo;
    private String testo;
    private List<Scelta> scelte;

    public Dialogo(String idDialogo, String testo, List<Scelta> scelte) {
        this.idDialogo = idDialogo;
        this.testo = testo;
        this.scelte = scelte;
    }

    public Dialogo(String idDialogo, String testo) {
        this.idDialogo = idDialogo;
        this.testo = testo;
        this.scelte = new ArrayList<>();
    }

    /** 
     * @return String
     */
    public String getIdDialogo() {
        return idDialogo;
    }

    /** 
     * @return String
     */
    public String getTesto() {
        return testo;
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
