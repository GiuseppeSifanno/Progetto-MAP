package progetto.gioco.game.manager;

import progetto.gioco.engine.database.DBManager;
import progetto.gioco.engine.manager.BaseGameManager;
import progetto.gioco.engine.manager.Startable;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseOggetto;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.game.database.*;
import progetto.gioco.game.loader.DialogLoader;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Inventario;
import progetto.gioco.game.model.StatoGioco;
import progetto.gioco.game.model.npc.BaseNPC;

public class GameManager extends BaseGameManager implements Startable, GameObserver {
    private boolean isRunning = false;
    private final StatoGioco gameState;

    public GameManager() {
        this.dbManager = new DBManager("config.properties");

        this.dialogManager = new DialogManager();
        this.inventarioManager = new InventarioManager(
                new OggettoDAO(), new MaterialeDAO(), new RicettaDAO()
        );
        this.puzzleManager = new PuzzleManager(new PuzzleDAO());
        this.saveManager = new SaveManager(new StatoGiocoDAO());

        // gameState condivide l'Inventario "vivo" di InventarioManager,
        // invece di tenerne una copia separata
        this.gameState = new StatoGioco(
                null,
                new java.util.ArrayList<>(),
                new Inventario(),
                new java.util.ArrayList<>()
        );

        // Registra observer
        ((DialogManager) dialogManager).addObserver(this);
        ((PuzzleManager) puzzleManager).addObserver(this);
        ((InventarioManager) inventarioManager).addObserver(this);
    }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case ATTO_CAMBIATO -> gameState.setIdAttoCorrente((String) evento.getPayload());
            case SCELTA_EFFETTUATA -> gameState.getScelteEffettuate().add(String.valueOf(evento.getPayload()));
            case PUZZLE_RISOLTO -> gameState.getPuzzleRisolti().add((String) evento.getPayload());
            case OGGETTO_AGGIUNTO -> gameState.getInventario().aggiungi(String.valueOf(evento.getPayload()));
            case OGGETTO_RIMOSSO -> {
                BaseOggetto oggetto = (BaseOggetto) evento.getPayload();
                if (oggetto != null) gameState.getInventario().rimuovi(oggetto.getId());
            }
            default -> { }
        }
    }

    @Override
    public void cambiaScena(String idAtto) {
        DialogLoader loader = new DialogLoader();
        BaseAtto<Dialogo> atto = loader.load("dialogs/"+ idAtto + ".json");
        dialogManager.setAtto(atto);
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
