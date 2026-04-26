package progetto.gioco.model;

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

    public String getIdDialogo() {
        return idDialogo;
    }

    public String getTesto() {
        return testo;
    }

    public List<Scelta> getScelte() {
        return scelte;
    }

    public int getNumeroScelte(){
        return scelte.size();
    }
}
