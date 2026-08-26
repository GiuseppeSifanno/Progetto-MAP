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

    // ============================================================
    // GESTIONE ATTO
    // ============================================================

    @Override
    public void setAtto(BaseAtto<Dialogo> atto) {

        this.atto = atto;

        if (atto == null) {
            this.dialoghi = null;
            impostaDialogoCorrente(null);
            return;
        }

        // Copia della mappa dei dialoghi dell'atto
        this.dialoghi = new HashMap<>(atto.getDialoghi());

        // Dialogo iniziale dell'atto
        Dialogo dialogoIniziale = dialoghi.get(atto.getDialogoIniziale());

        impostaDialogoCorrente(dialogoIniziale);

        // Notifica cambio atto
        notifyObservers(new GameEvent(
                TipoEvento.ATTO_CAMBIATO,
                atto.getId()
        ));
    }

    // ============================================================
    // AVVIO DIALOGO
    // ============================================================

    @Override
    public void startDialogo(String idDialogo) {

        if (dialoghi == null) {
            System.err.println(
                    "DialogManager: impossibile avviare il dialogo '" +
                            idDialogo +
                            "': nessun dialogo caricato."
            );
            return;
        }

        Dialogo dialogo = dialoghi.get(idDialogo);

        if (dialogo == null) {
            System.err.println(
                    "DialogManager: dialogo non trovato: " + idDialogo
            );
            return;
        }

        impostaDialogoCorrente(dialogo);
    }

    // ============================================================
    // GETTER
    // ============================================================

    @Override
    public BaseDialogo getDialogo() {
        return corrente;
    }

    @Override
    public BaseDialogo getDialogoById(String id) {

        if (dialoghi == null) {
            return null;
        }

        return dialoghi.get(id);
    }

    @Override
    public BaseAtto<Dialogo> getAtto() {
        return this.atto;
    }

    // ============================================================
    // SCELTA
    // ============================================================

    public BaseScelta scegliOpzione(int index) {
        if (corrente == null || corrente.getScelte().isEmpty()) {
            impostaDialogoCorrente(null);
            return null;
        }

        String idDialogoCorrente = corrente.getId();
        Scelta scelta = corrente.getScelte().get(index);
        String nextId = scelta.getNext();
        Dialogo prossimo;

        if (nextId == null || nextId.isBlank()) {
            prossimo = dialoghi.get(corrente.getNextId());
        } else {
            prossimo = dialoghi.get(nextId);
        }

        impostaDialogoCorrente(prossimo);
        GameEvent event = new GameEvent(
                TipoEvento.SCELTA_EFFETTUATA,
                new SceltaEffettuata(idDialogoCorrente, scelta.getId())
        );

        notifyObservers(event);
        autoAvanza();
        return scelta;
    }

    // ============================================================
    // AUTO AVANZAMENTO
    // ============================================================

    /**
     * Se il dialogo corrente non ha scelte e ha un nextId definito,
     * avanza automaticamente al dialogo successivo.
     */
    @Override
    public void autoAvanza() {
        while (corrente != null
                && corrente.getNumeroScelte() == 0
                && corrente.getNextId() != null
                && !corrente.getNextId().isEmpty()) {

            impostaDialogoCorrente(
                    dialoghi.get(corrente.getNextId())
            );
        }
    }

    // ============================================================
    // PROSSIMO DIALOGO
    // ============================================================
    @Override
    public void prossimoDialogo() {
        if (corrente == null) {
            return;
        }
        String nextId = corrente.getNextId();
        if (nextId == null || nextId.isBlank()) {
            impostaDialogoCorrente(null);
            return;
        }
        impostaDialogoCorrente(dialoghi.get(nextId));
    }

    // ============================================================
    // METODO CENTRALE PER CAMBIARE DIALOGO
    // ============================================================

    /**
     * Cambia il dialogo corrente e notifica sempre la GUI.
     *
     * Questo è il punto centrale attraverso cui deve passare
     * qualsiasi modifica del dialogo corrente.
     */
    private void impostaDialogoCorrente(Dialogo nuovoDialogo) {
        this.corrente = nuovoDialogo;
        notifyObservers(new GameEvent(
                TipoEvento.DIALOGO_CAMBIATO,
                nuovoDialogo
        ));
    }

    // ============================================================
    // CICLO DI VITA
    // ============================================================

    @Override
    public void init() {
        // Non serve inizializzare nulla qui.
    }

    @Override
    public void reset() {
        this.dialoghi = null;
        this.corrente = null;
        /*
         * NON eliminiamo gli observer.
         */
    }

    // ============================================================
    // OBSERVER
    // ============================================================

    @Override
    public void addObserver(GameObserver observer) {

        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
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

        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}