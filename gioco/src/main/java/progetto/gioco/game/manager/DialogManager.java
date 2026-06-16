package progetto.gioco.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import progetto.gioco.engine.manager.BaseDialogManager;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseScelta;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.GameObservable;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.SceltaEffettuata;

public class DialogManager extends BaseDialogManager implements GameObservable {
    private List<GameObserver> observers;
    
    //contiene tutti i dialoghi di un atto specifico
    private Map<String, Dialogo> dialoghi;
    
    //id corrente del dialogo
    private Dialogo dialogoCorrente;

    //vengono salvate le scelte fatte
    private List<SceltaEffettuata> scelteEffettuate;
    
    private Atto atto;

    public DialogManager() {
        this.observers = new ArrayList<GameObserver>(1);
        this.dialoghi = new HashMap<>();
        this.scelteEffettuate = new ArrayList<>();
    }

    /** 
     * @param atto
     */
    public void setAtto(Atto atto){
        this.atto = atto;
    }

    /** 
     * @return Dialogo
     */
    //recupera il dialogo corrente
    public Dialogo getDialogo(){
        return dialogoCorrente;
    }

    /** 
     * @param scelta
     * @return Scelta
     */
    //scelta opzione dialogo
    public BaseScelta scegliOpzione(int scelta){
        if(scelta <= 0  || scelta > dialogoCorrente.getNumeroScelte())
            throw new IllegalArgumentException("Scelta non valida");

        BaseScelta s = dialogoCorrente.getScelte().get(scelta - 1);
        String next = s.getNext();

        //aggiunge la scelta effettuata prima di cambiare il dialogo corrente
        scelteEffettuate.add(new SceltaEffettuata(s.getIdScelta(), dialogoCorrente.getIdDialogo()));

        //significa che ci sono altri dialoghi
        if (next != null)
            //recupera il dialogo successivo attraverso l'id
            dialogoCorrente = dialoghi.get(next);
        else
            dialogoCorrente = null;

        GameEvent event = new GameEvent();
        event.setTipo(TipoEvento.SCELTA_EFFETTUATA);
        event.setPayload(s.getIdScelta());
        notifyObservers(event);

        return s;
    }

    public List<SceltaEffettuata> getScelteEffettuate() {
        return scelteEffettuate;
    }

    @Override
    public void startDialogo(BaseAtto atto) {
        this.atto = (Atto) atto;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void reset() {
        this.dialoghi.clear();
        this.dialogoCorrente = null;
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(GameObserver observer) {
    }

    private void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}
