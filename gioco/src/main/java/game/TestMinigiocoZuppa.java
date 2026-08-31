package game;

import engine.observer.GameEvent;
import engine.observer.GameObserver;
import game.manager.GameManager;
import game.model.minigioco.Erba;
import game.model.minigioco.ZuppaFogliantiConfig;
import game.minigioco.ZuppaFogliantiManager;

import java.util.List;

public class TestMinigiocoZuppa {
    public static void main(String[] args) throws InterruptedException {
        GameManager gameManager = new GameManager();
        gameManager.init();

        // --- Config di prova (in futuro caricata da JSON, per ora hardcoded) ---
        ZuppaFogliantiConfig config = new ZuppaFogliantiConfig(
                List.of(
                        new Erba("erba1", "Radice buona", true),
                        new Erba("erba2", "Fungo velenoso", false),
                        new Erba("erba3", "Radice buona 2", true),
                        new Erba("erba4", "Bacca velenosa", false),
                        new Erba("erba5", "Radice buona 3", true)
                ),
                3,      // erbeCorretteRichieste
                40,     // zonaVerdeMin
                60,     // zonaVerdeMax
                3,      // colpiRichiesti
                50,     // velocitaIndicatoreMs (più lento del default, per testare a mano)
                "o10",  // id oggetto tazza da tè (assumendo l'abbiate inserito così a DB)
                "o12"   // oggetto zuppa
        );

        ZuppaFogliantiManager zuppaManager = new ZuppaFogliantiManager(
                config,
                gameManager.getInventarioManager(),
                gameManager.getDialogManager()
        );

        // observer "spia" per stampare tutti gli eventi del minigioco
        zuppaManager.addObserver(new GameObserver() {
            @Override
            public void onEvent(GameEvent evento) {
                System.out.println("[" + evento.getTipo() + "] " + evento.getPayload());
            }
        });

        System.out.println("=== AVVIO MINIGIOCO ===");
        zuppaManager.avviaMinigioco();

        // --- FASE NAVIGATRICE: simulo i click sulle erbe ---
        System.out.println("=== FASE NAVIGATRICE ===");
        zuppaManager.onErbaSelezionata("erba2"); // sbagliata, solo log
        zuppaManager.onErbaSelezionata("erba1"); // corretta 1/3
        zuppaManager.onErbaSelezionata("erba3"); // corretta 2/3
        zuppaManager.onErbaSelezionata("erba5"); // corretta 3/3 -> passa a Combattente

        // --- FASE COMBATTENTE: il thread sta già girando, "premo" a intervalli ---
        System.out.println("=== FASE COMBATTENTE (premo 3 volte a caso, non garantito) ===");
        for (int i = 0; i < 3; i++) {
            Thread.sleep(120); // aspetto che l'indicatore si sposti
            zuppaManager.onPressioneCombattente();
        }

        // se non sono riuscito 3 volte nella zona verde, continuo a tentare
        int tentativiMax = 50;
        int tentativi = 0;
        while (tentativi < tentativiMax) {
            Thread.sleep(50);
            zuppaManager.onPressioneCombattente();
            tentativi++;
        }

        // --- FASE CAPITANO ---
        System.out.println("=== FASE CAPITANO ===");
        // assicurati che la tazza da tè sia in inventario, altrimenti aggiungila per test
        gameManager.getInventarioManager().aggiungiOggettoDaId("o10");
        zuppaManager.onOggettoUsato("o10");

        System.out.println("=== FINE TEST ===");
        System.out.println("Oggetto zuppa presente? " +
                gameManager.getInventarioManager().hasOggetto("o12"));

        // pulizia: ferma eventuali thread ancora attivi
        zuppaManager.reset();
    }
}