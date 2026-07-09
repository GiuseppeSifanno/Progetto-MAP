package progetto.gioco.game.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import progetto.gioco.engine.manager.BaseDialogManager;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.GameObservable;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Scelta;

public class DialogManager extends BaseDialogManager<Dialogo> implements GameObservable {
    private final List<GameObserver> observers = new ArrayList<>();
    private Map<String, Dialogo> dialoghi;
    private Dialogo corrente;

    @Override
    public void setAtto(BaseAtto<Dialogo> atto) {
        this.atto = atto;
        this.dialoghi = atto.getDialoghi();
        this.corrente = dialoghi.get(atto.getDialogoIniziale());

        GameEvent event = new GameEvent(TipoEvento.ATTO_CAMBIATO, atto.getIdAtto());
        notifyObservers(event);
    }

    @Override
    public void startDialogo(String idDialogo) {
        this.dialoghi = atto.getDialoghi();
        this.corrente = dialoghi.get(idDialogo);
    }

    @Override
    public Dialogo getDialogo() {
        return corrente;
    }

    @Override
    public Scelta scegliOpzione(int index) {
        if (corrente.getScelte().isEmpty()) {
            corrente = null;
            return null;
        }
        Scelta scelta = corrente.getScelte().get(index);
        String nextId = scelta.getNext();
        corrente = nextId.equalsIgnoreCase("") ? dialoghi.get(corrente.getNextId()) : dialoghi.get(nextId);
        autoAvanza();
        return scelta;
    }

    /**
     * Se il dialogo corrente non ha scelte e ha un nextId definito,
     * avanza automaticamente al dialogo successivo.
     */
    private void autoAvanza() {
        while (corrente != null && corrente.getNumeroScelte() == 0
                && corrente.getNextId() != null && !corrente.getNextId().isEmpty()) {
            corrente = dialoghi.get(corrente.getNextId());
        }
    }

    @Override
    public void init() {
        // I dialoghi vengono caricati quando servono
    }

    @Override
    public void reset() {
        this.dialoghi.clear();
        this.corrente = null;
        this.dialogoCorrente = null;
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(GameObserver observer, GameEvent evento) {
        observer.onEvent(evento);
    }

    private void notifyObservers(GameEvent event) {
        for (GameObserver o : observers) {
            o.onEvent(event);
        }
    }
}