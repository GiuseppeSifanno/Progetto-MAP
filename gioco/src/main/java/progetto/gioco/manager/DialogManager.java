package progetto.gioco.manager;

import progetto.gioco.model.Atto;
import progetto.gioco.model.SceltaEffettuata;

import java.util.List;
import java.util.Map;

public class DialogManager {
    //contiene tutti i dialoghi di un atto specifico
    private Map<String, Dialogo> dialoghi;

    //vengono salvate le scelte fatte
    private List<SceltaEffettuata> scelteEffettuate;

    //id corrente del dialogo
    private String dialogoCorrente;

    //carica un atto
    public void caricaAtto(Atto atto){
        //logica per caricare l'atto
    }

    //recupera il dialogo corrente
    public Dialogo getDialogo(){
        return dialoghi.get(dialogoCorrente);
    }

    //scelta opzione dialogo
    public void scegliOpzione(int scelta){
        //recupero l'istanza del dialogo corrente
        Dialogo corrente = dialoghi.get(dialogoCorrente);

        if(scelta <= 0  || scelta > corrente.getNumeroScelte())
            throw new IllegalArgumentException("Scelta non valida");

        this.scelta.add(new SceltaEffettuata(dialogoCorrente, scelta));

        //significa che non ci sono altri dialoghi
        String next = corrente.getNext(scelta);
        if (next != null)
            dialogoCorrente = next;
    }
}
