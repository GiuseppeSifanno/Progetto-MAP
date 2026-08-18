package game.manager;

import engine.database.DBManager;
import engine.manager.BaseGameManager;
import engine.manager.Startable;
import engine.model.BaseAtto;
import engine.model.BaseOggetto;
import engine.observer.GameEvent;
import engine.observer.GameObserver;
import game.database.*;
import game.loader.DialogLoader;
import game.model.*;
import game.model.npc.BaseNPC;

public class GameManager extends BaseGameManager implements Startable, GameObserver {
    private boolean isRunning = false;
    private final StatoGioco gameState;

    public GameManager() {
        this.dbManager = new DBManager("config.properties");
        MaterialeDAO materialeDAO = new MaterialeDAO(dbManager);
        OggettoDAO oggettoDAO = new OggettoDAO(dbManager);
        RicettaDAO ricettaDAO = new RicettaDAO(dbManager);
        PuzzleDAO puzzleDAO = new PuzzleDAO(dbManager);

        this.dialogManager = new DialogManager();
        this.inventarioManager = new InventarioManager(
                oggettoDAO,
                materialeDAO,
                ricettaDAO
        );
        this.puzzleManager = new PuzzleManager(puzzleDAO);
        this.saveManager = new SaveManager(new StatoGiocoDAO(dbManager, materialeDAO, oggettoDAO));

        // gameState condivide l'Inventario "vivo" di InventarioManager,
        // invece di tenerne una copia separata
        this.gameState = new StatoGioco(
                null,
                null,
                new java.util.ArrayList<>(),
                new java.util.ArrayList<>(),
                new Inventario(),
                new java.util.ArrayList<>()
        );

        // Registra observer
        ((DialogManager) dialogManager).addObserver(this);
        ((PuzzleManager) puzzleManager).addObserver(this);
        ((InventarioManager) inventarioManager).addObserver(this);
    }

    public StatoGioco getGameState() { return gameState; }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case ATTO_CAMBIATO -> gameState.setIdAttoCorrente((String) evento.getPayload());
            case SCELTA_EFFETTUATA -> gameState.getScelteEffettuate().add((SceltaEffettuata) evento.getPayload());
            case PUZZLE_RISOLTO -> gameState.getPuzzleRisolti().add(String.valueOf(evento.getPayload()));
            case OGGETTO_AGGIUNTO -> gameState.getInventario().aggiungi((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO -> {
                BaseOggetto oggetto = (BaseOggetto) evento.getPayload();
                if (oggetto != null) gameState.getInventario().rimuovi(oggetto.getId());
            }
            case DIALOGO_CAMBIATO -> gameState.setIdDialogoCorrente((String) evento.getPayload());
            case QUEST_COMPLETATA -> gameState.aggiungiQuestCompletata((PassoQuestCompletato) evento.getPayload());
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
