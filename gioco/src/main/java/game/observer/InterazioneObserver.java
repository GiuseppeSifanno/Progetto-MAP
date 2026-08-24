package game.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import engine.manager.BaseInterazioneObserver;
import engine.observer.*;
import game.loader.InterazioniLoader;
import game.manager.DialogManager;
import game.manager.InventarioManager;
import game.model.Interazione;
import game.model.Zona;

/**
 * Observer che gestisce le interazioni della zona corrente.
 */
public class InterazioneObserver extends BaseInterazioneObserver {
    private final List<GameObserver> observers = new ArrayList<>();

    private final InventarioManager inventarioManager;
    private final DialogManager dialogManager;

    public InterazioneObserver(InventarioManager inventarioManager, DialogManager dialogManager) {
        this.inventarioManager = inventarioManager;
        this.dialogManager = dialogManager;
    }

    @Override
    public void tentaInterazione(String id) {
        Interazione interazione = (Interazione) interazioni.get(id);
        if (interazione == null) return;

        boolean condizioniSoddisfatte = interazione.getCondizioni().stream()
                .allMatch(inventarioManager::hasOggetto);

        if (condizioniSoddisfatte) {
            applicaEffetti(interazione.getEffetti());
            notifyObservers(new GameEvent(
                    TipoEvento.MESSAGGIO_MOSTRATO,
                    interazione.getMessaggioSbloccato()
            ));
        } else {
            notifyObservers(new GameEvent(
                    TipoEvento.MESSAGGIO_MOSTRATO,
                    interazione.getMessaggioBloccato()
            ));
        }
    }

    /**
     * Carica le zone dell'atto corrente. Le interazioni del vecchio atto
     * vengono eliminate per evitare che restino interagibili dopo il cambio scena.
     */
    public void caricaZone(List<String> nomiZone) {
        interazioni.clear();

        for (String nome : nomiZone) {
            if (nome == null || nome.isBlank()) continue;
            Zona zona = new InterazioniLoader().load("zone/" + nome + ".json");
            this.interazioni.putAll(zona.getInterazioni());
        }
    }

    /**
     * Permette al sistema dei minigiochi/GUI di aggiungere un flag di stato.
     * I flag sono modellati come oggetti tecnici, secondo il contratto attuale
     * delle zone.
     * @param idFlag id dell'oggetto tecnico usato come flag
     */
    public void impostaFlag(String idFlag) {
        inventarioManager.aggiungiOggettoDaId(idFlag);
    }

    /** Restituisce le interazioni attualmente disponibili nella zona dell'atto. */
    public java.util.Map<String, Interazione> getInterazioni() {
        return java.util.Collections.unmodifiableMap(interazioni);
    }

    private void applicaEffetti(List<Effetto> effetti) {
        for (Effetto effetto : effetti) {
            applicaEffetto(effetto);
        }
    }

    private void applicaEffetto(Effetto effetto) {
        switch (effetto.tipo()) {
            case AGGIUNGI_OGGETTO -> inventarioManager.aggiungiOggettoDaId(effetto.valore());
            case AVVIA_DIALOGO -> dialogManager.startDialogo(effetto.valore());
            case RIMUOVI_OGGETTO -> inventarioManager.rimuoviOggetto(effetto.valore());
            case PROSSIMO_ATTO -> notifyObservers(
                    new GameEvent(TipoEvento.ATTO_COMPLETATO, null)
            );
        }
    }

    @Override
    public void init() {
        this.interazioni = new HashMap<>();
    }

    @Override
    public void reset() {
        if (interazioni != null) interazioni.clear();
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
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}
