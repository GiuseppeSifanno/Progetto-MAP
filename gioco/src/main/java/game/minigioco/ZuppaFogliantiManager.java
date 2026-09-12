package game.minigioco;

import engine.manager.BaseInventarioManager;
import engine.model.BaseOggetto;
import engine.observer.GameEvent;
import engine.observer.GameObservable;
import engine.observer.GameObserver;
import engine.observer.TipoEvento;
import game.database.RicettaDAO;
import game.manager.InventarioManager;
import game.model.PassoQuestCompletato;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce lo stato del minigioco Zuppa Foglianti.
 * @author Graziano
 * @author Giuseppe
 */
public class ZuppaFogliantiManager implements GameObservable, GameObserver {
    private final List<GameObserver> observers = new ArrayList<>();

    private final InventarioManager inventarioManager;
    private final RicettaDAO ricettaDAO;

    public record EsitoErba(String idErba, boolean corretta, int numeroErbe) {}

    private int numeroErbe = 0;

    public ZuppaFogliantiManager(BaseInventarioManager inventarioManager, RicettaDAO ricettaDAO) {
        this.inventarioManager = (InventarioManager) inventarioManager;
        this.ricettaDAO = ricettaDAO;
    }

    public void avviaMinigioco() {
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_AVVIATO, null));
    }

    public void onErbaSelezionata(String idErba) {
        boolean corretta = ricettaDAO.findAll()
                .stream()
                .anyMatch(r -> r.getIngredienti().contains(idErba));

        if (corretta) {
            inventarioManager.aggiungiOggettoDaId(idErba);

            numeroErbe++;

            notifyObservers(new GameEvent(
                    TipoEvento.MINIGIOCO_ERBA_ESITO,
                    new EsitoErba(idErba, true, numeroErbe)
            ));
        } else {
            notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_ERBA_ESITO,
                    new EsitoErba(idErba, false, numeroErbe)
            ));
        }
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(GameObserver observer, GameEvent evento) {
        observer.onEvent(evento);
    }

    @Override
    public void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            observer.onEvent(event);
        }
    }

    @Override
    public void onEvent(GameEvent evento) {
        if (evento.getTipo() == TipoEvento.OGGETTO_AGGIUNTO) {
            BaseOggetto oggetto = (BaseOggetto) evento.getPayload();
            if (oggetto != null && "o12".equals(oggetto.getId())) {
                notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COMPLETATO, "int_giungla_zuppa_completata"));
            }
        }
    }
}