package progetto.gioco.game.manager;

import progetto.gioco.database.DBManager;
import progetto.gioco.engine.manager.BaseGameManager;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.loader.DialogLoader;
import progetto.gioco.game.model.Atto;

public class GameManager extends BaseGameManager {
    public GameManager() {
        this.dialogManager = new DialogManager();
        this.inventarioManager = new InventarioManager();
        this.puzzleManager = new PuzzleManager();
        this.saveManager = new SaveManager();
        this.dbManager = new DBManager("config.properties");
        // Aggiungere sezione GUI

        // Registra observer
    }

    @Override
    public void startGame() {
        // Carica il primo atto
        DialogLoader loader = new DialogLoader();
        Atto atto = loader.load("dialogs/atto1.json");
        dialogManager.setAtto(atto);

        // Sezione GUI

        // Listener GUI per avvio partita

        // Faccio partire tutto
        dialogManager.startDialogo("d1");
    }

    @Override
    public void cambiaScena(String idAtto) {
        DialogLoader loader = new DialogLoader();
        Atto atto = loader.load("dialogs/"+ idAtto + ".json");
        dialogManager.setAtto(atto);

        new GameEvent(TipoEvento.ATTO_CAMBIATO, idAtto);
    }
    @Override
    public void init() {
        dbManager.init();
        dialogManager.init();
        inventarioManager.init();
        puzzleManager.init();
        saveManager.init();
    }

    @Override
    public void reset() {
        dbManager.reset();
        dialogManager.reset();
        inventarioManager.reset();
        puzzleManager.reset();
        saveManager.reset();
    }
}
