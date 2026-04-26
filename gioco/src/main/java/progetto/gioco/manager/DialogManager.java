package progetto.gioco.manager;

import progetto.gioco.model.Atto;
import progetto.gioco.model.Dialogo;
import progetto.gioco.model.Scelta;
import progetto.gioco.model.SceltaEffettuata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogManager {
    //contiene tutti i dialoghi di un atto specifico
    private Map<String, Dialogo> dialoghi;

    //vengono salvate le scelte fatte
    private List<SceltaEffettuata> scelteEffettuate;

    //id corrente del dialogo
    private Dialogo dialogoCorrente;

    //fa partire un dialogo di un atto
    public void startDialogo(Atto atto) {
        this.dialoghi = atto.getDialoghi();
        this.dialogoCorrente = dialoghi.get(atto.getDialogoIniziale());
        this.scelteEffettuate = new ArrayList<>();
    }
    //recupera il dialogo corrente
    public Dialogo getDialogo(){
        return dialogoCorrente;
    }

    //scelta opzione dialogo
    public Scelta scegliOpzione(int scelta){
        if(scelta <= 0  || scelta > dialogoCorrente.getNumeroScelte())
            throw new IllegalArgumentException("Scelta non valida");


        Scelta s = dialogoCorrente.getScelte().get(scelta - 1);
        String next = s.getNext();

        //aggiunge la scelta effettuata prima di cambiare il dialogo corrente
        scelteEffettuate.add(new SceltaEffettuata(s.getIdScelta(), dialogoCorrente.getIdDialogo()));

        //significa che ci sono altri dialoghi
        if (next != null)
            //recupera il dialogo successivo attraverso l'id
            dialogoCorrente = dialoghi.get(next);
        else
            dialogoCorrente = null;

        return s;
    }
}
