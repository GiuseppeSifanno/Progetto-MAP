package game;

import engine.model.BaseOggetto;
import engine.observer.GameEvent;
import engine.observer.GameObserver;
import game.manager.GameManager;
import game.model.oggetti.Oggetto;

public class TestInterazioni {
    public static void main(String[] args) {
        GameManager gameManager = new GameManager();
        gameManager.init();

        // observer "spia" per stampare i messaggi mostrati
        gameManager.getInterazioneObserver().addObserver(new GameObserver() {
            @Override
            public void onEvent(GameEvent evento) {
                System.out.println("[" + evento.getTipo() + "] " + evento.getPayload());
            }
        });

        System.out.println("--- Tentativo SENZA condizioni soddisfatte ---");
        gameManager.getInterazioneObserver().tentaInterazione("int_legnetti");

        System.out.println("--- Aggiungo lente e foglie all'inventario ---");
        gameManager.getInventarioManager().aggiungiOggettoDaId("lente");
        gameManager.getInventarioManager().aggiungiOggettoDaId("foglie");

        System.out.println("--- Tentativo CON condizioni soddisfatte ---");
        gameManager.getInterazioneObserver().tentaInterazione("int_legnetti");

        for (BaseOggetto o: gameManager.getGameState().getInventario().oggetti()){
            System.out.println("Oggetto: "+ o.getId() + " " + o.getNome() +  " " + o.getDescrizione());
        }

    }
}