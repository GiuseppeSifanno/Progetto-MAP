package progetto.gioco.game.manager;

import progetto.gioco.engine.database.DBManager;
import progetto.gioco.engine.manager.BaseGameManager;
import progetto.gioco.engine.manager.Startable;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.controller.GameState;
import progetto.gioco.game.loader.DialogLoader;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.npc.BaseNPC;

public class GameManager extends BaseGameManager implements Startable {
    private boolean isRunning = false;
    private final GameState gameState;

    public GameManager() {
        this.gameState = new GameState();

        this.dialogManager = new DialogManager();
        this.inventarioManager = new InventarioManager();
        this.puzzleManager = new PuzzleManager();
        this.saveManager = new SaveManager();
        this.dbManager = new DBManager("config.properties");
        // Aggiungere sezione GUI

        // Registra observer
    }

    @Override
    public void cambiaScena(String idAtto) {
        DialogLoader loader = new DialogLoader();
        BaseAtto<Dialogo> atto = loader.load("dialogs/"+ idAtto + ".json");
        dialogManager.setAtto(atto);

        new GameEvent(TipoEvento.ATTO_CAMBIATO, idAtto);
    }

    /**
     * @param npc NPC con cui si interagisce
     */
    public void interagisci(BaseNPC npc){
        //recupero il dialogo per un certo contesto di gioco
        String idDialogo = npc.getIdDialogo();
        if (idDialogo.isEmpty()) return;
        //fa partire il dialogo con un certo id
        dialogManager.startDialogo(idDialogo);
    }

    @Override
    public void start() {
        // Carica il primo atto
        DialogLoader loader = new DialogLoader();
        Atto atto = loader.load("dialogs/atto1.json");
        dialogManager.setAtto(atto);

        // Sezione GUI

        // Listener GUI per avvio partita

        // Faccio partire tutto
        isRunning = true;

        dialogManager.startDialogo("d1");
    }

    @Override
    public void stop() {
        isRunning = false;
        this.reset();
        System.exit(0);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
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
