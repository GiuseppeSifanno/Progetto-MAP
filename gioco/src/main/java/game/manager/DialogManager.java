package game.manager;

import engine.manager.BaseDialogManager;
import engine.model.BaseAtto;
import engine.model.BaseDialogo;
import engine.model.BaseScelta;
import engine.observer.GameEvent;
import engine.observer.GameObservable;
import engine.observer.GameObserver;
import engine.observer.TipoEvento;
import game.model.Dialogo;
import game.model.Scelta;
import game.model.SceltaEffettuata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogManager extends BaseDialogManager<Dialogo> implements GameObservable {
    private final List<GameObserver> observers = new ArrayList<>();
    private Map<String, Dialogo> dialoghi;
    private Dialogo corrente;

    @Override
    public void setAtto(BaseAtto<Dialogo> atto) {
        this.atto = atto;
        // copia senza riferimento diretto
        this.dialoghi = new HashMap<>(atto.getDialoghi());
        this.corrente = dialoghi.get(atto.getDialogoIniziale());

        GameEvent event = new GameEvent(TipoEvento.ATTO_CAMBIATO, atto.getId());
        notifyObservers(event);

        event = new GameEvent(TipoEvento.DIALOGO_CAMBIATO, this.corrente);
        notifyObservers(event);
    }

    @Override
    public void startDialogo(String idDialogo) {
        this.dialoghi = atto.getDialoghi();
        this.corrente = dialoghi.get(idDialogo);
    }

    @Override
    public BaseDialogo getDialogo() { return corrente; }

    @Override
    public BaseDialogo getDialogoById(String id) { return dialoghi.get(id); }

    public BaseScelta scegliOpzione(int index) {
        if (corrente.getScelte().isEmpty()) {
            corrente = null;
            return null;
        }

        String idDialogoCorrente = corrente.getId();

        Scelta scelta = corrente.getScelte().get(index);
        String nextId = scelta.getNext();
        corrente = nextId.isBlank() ? dialoghi.get(corrente.getNextId()) : dialoghi.get(nextId);

        GameEvent event = new GameEvent(
                TipoEvento.SCELTA_EFFETTUATA,
                new SceltaEffettuata(idDialogoCorrente, scelta.getId())
        );
        notifyObservers(event);

        autoAvanza();
        return scelta;
    }

    /**
     * Se il dialogo corrente non ha scelte e ha un nextId definito,
     * avanza automaticamente al dialogo successivo.
     */
    @Override
    public void autoAvanza() {
        while (corrente != null && corrente.getNumeroScelte() == 0
                && corrente.getNextId() != null && !corrente.getNextId().isEmpty()) {
            corrente = dialoghi.get(corrente.getNextId());
            if (corrente != null)
                notifyObservers(new GameEvent(TipoEvento.DIALOGO_CAMBIATO, corrente));
        }
    }

    @Override
    public void prossimoDialogo(){
        if (corrente == null) {
            return;
        }

        String nextId = corrente.getNextId();
        if (nextId == null || nextId.isBlank()) {
            corrente = null;
            return;
        }

        corrente = dialoghi.get(nextId);
        if (corrente != null) {
            notifyObservers(new GameEvent(TipoEvento.DIALOGO_CAMBIATO, corrente));
        }
    }

    @Override
    public BaseAtto<Dialogo> getAtto() {
        return this.atto;
    }

    @Override
    public void init() {
        // I dialoghi vengono caricati quando servono
    }

    @Override
    public void reset() {
        if (this.dialoghi != null)
            this.dialoghi.clear();
        this.corrente = null;
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