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
 * Observer che gestisce le interazioni.
 */
public class InterazioneObserver extends BaseInterazioneObserver {
    private final List<GameObserver> observers = new ArrayList<>();

    private final InventarioManager inventarioManager;
    private final DialogManager dialogManager;

    /**
     * Costruttore.
     * @param inventarioManager Riferimento al manager dell'inventario.
     * @param dialogManager Riferimento al manager dei dialogi.
     */
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
            notifyObservers(new GameEvent(TipoEvento.MESSAGGIO_MOSTRATO, interazione.getMessaggioSbloccato()));
        } else {
            notifyObservers(new GameEvent(TipoEvento.MESSAGGIO_MOSTRATO, interazione.getMessaggioBloccato()));
        }
    }

    /**
     * Applica gli effetti all'inventario e ai dialoghi.
     * @param effetti Lista degli effetti da applicare
     */
    private void applicaEffetti(List<Effetto> effetti) {
        for (Effetto effetto : effetti) {
            applicaEffetto(effetto);
        }
    }

    /**
     * Applica un effetto all'inventario e ai dialoghi.
     * @param effetto Effetto da applicare
     */
    private void applicaEffetto(Effetto effetto) {
        switch (effetto.tipo()) {
            case AGGIUNGI_OGGETTO -> inventarioManager.aggiungiOggettoDaId(effetto.valore());
            case AVVIA_DIALOGO -> dialogManager.startDialogo(effetto.valore());
            case RIMUOVI_OGGETTO -> inventarioManager.rimuoviOggetto(effetto.valore());
        }
    }

    @Override
    public void init() {
        //TODO modificare il nome del file in caso di cambiamento
        Zona zona = new InterazioniLoader().load("zone/spiaggia.json");
        this.interazioni = new HashMap<>(zona.getInterazioni());
    }

    @Override
    public void reset() {
        interazioni.clear();
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

    /**
     * Notifica a tutti gli observer registrati un evento.
     * @param event Evento da notificare agli observer
     */
    private void notifyObservers(GameEvent event) {
        for (GameObserver o : observers) {
            o.onEvent(event);
        }
    }
}