package game.manager;

import engine.database.DBManager;
import engine.manager.BaseGameManager;
import engine.manager.Startable;
import engine.model.BaseAtto;
import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import engine.model.Inventario;
import engine.observer.GameEvent;
import engine.observer.GameObserver;
import game.database.*;
import game.loader.DialogLoader;
import game.loader.QuestLoader;
import game.model.*;
import game.observer.GUIObserver;
import game.observer.InterazioneObserver;
import game.gui.GameUIListener;
import game.rest.WikiServer;

import java.sql.SQLException;
import java.util.*;

public class GameManager extends BaseGameManager implements Startable, GameObserver {
    private boolean isRunning = false;
    private final StatoGioco gameState;
    private final InterazioneObserver interazioneObserver;
    private final Map<String, Quest> quest;

    private static final int PORTA_WIKI = 8080;
    private final WikiServer wikiServer;

    private static final List<String> SEQUENZA_ATTI =
            List.of("a0", "a1", "a2", "a3", "a4", "a5");

    private static final Map<String, List<String>> ZONE_PER_ATTO = Map.of(
            "a0", List.of(),
            "a1", List.of("spiaggia"),
            "a2", List.of("giungla"),
            "a3", List.of("miniera"),
            "a4", List.of("vulcano"),
            "a5", List.of()
    );

    private int indiceAtto = 0;

    public GameManager() {
        this.dbManager = new DBManager("config.properties");
        this.wikiServer = new WikiServer(dbManager, PORTA_WIKI);
        this.quest = new LinkedHashMap<>();

        MaterialeDAO materialeDAO = new MaterialeDAO(dbManager);
        OggettoDAO oggettoDAO = new OggettoDAO(dbManager);
        RicettaDAO ricettaDAO = new RicettaDAO(dbManager);
        PuzzleDAO puzzleDAO = new PuzzleDAO(dbManager);

        this.inventarioManager = new InventarioManager(
                oggettoDAO,
                materialeDAO,
                ricettaDAO
        );
        this.dialogManager = new DialogManager();

        this.interazioneObserver = new InterazioneObserver(
                (InventarioManager) inventarioManager,
                (DialogManager) dialogManager,
                this.quest
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
        interazioneObserver.addObserver(this);
    }

    public void collegaGUI(GameUIListener listener) {
        GUIObserver guiObserver = new GUIObserver(listener);
        ((DialogManager) dialogManager).addObserver(guiObserver);
        ((InventarioManager) inventarioManager).addObserver(guiObserver);
        ((PuzzleManager) puzzleManager).addObserver(guiObserver);
        interazioneObserver.addObserver(guiObserver);
    }

    /**
     * Restituisce lo stato di gioco corrente.
     * @implNote Sola lettura: le liste esposte sono immutabili e {@code Inventario}
     * va sempre modificato tramite {@code InventarioManager}, mai direttamente da qui.
     * Scrivere qui bypassa il sistema di eventi (Observer) e disallinea gli osservatori.
     * @return StatoGioco
     */
    public StatoGioco getGameState() { return gameState; }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case ATTO_CAMBIATO      -> gameState.setIdAttoCorrente((String) evento.getPayload());
            case SCELTA_EFFETTUATA  -> gameState.aggiungiSceltaEffettuata((SceltaEffettuata) evento.getPayload());
            case PUZZLE_RISOLTO     -> gameState.aggiungiPuzzleRisolto(String.valueOf(evento.getPayload()));
            case OGGETTO_AGGIUNTO   -> gameState.getInventario().aggiungi((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO    -> {
                BaseOggetto oggetto = (BaseOggetto) evento.getPayload();
                if (oggetto != null) gameState.getInventario().rimuovi(oggetto.getId());
            }
            case DIALOGO_CAMBIATO -> {
                BaseDialogo dialogo = (BaseDialogo) evento.getPayload();
                if (dialogo == null) {
                    gameState.setIdDialogoCorrente(null);
                } else {
                    gameState.setIdDialogoCorrente(dialogo.getId());
                }
            }
            case QUEST_COMPLETATA   -> gameState.aggiungiQuestCompletata((PassoQuestCompletato) evento.getPayload());
            case ATTO_COMPLETATO    -> prossimoAtto();
            default -> { }
        }
    }

    @Override
    public void cambiaScena(String idAtto) {
        if (!SEQUENZA_ATTI.contains(idAtto)) {
            throw new IllegalArgumentException("Atto non presente nella sequenza: " + idAtto);
        }

        indiceAtto = SEQUENZA_ATTI.indexOf(idAtto);

        DialogLoader loader = new DialogLoader();
        BaseAtto<Dialogo> atto = loader.load("dialogs/" + idAtto + ".json");
        ((DialogManager) dialogManager).setAtto(atto);

        interazioneObserver.caricaZone(ZONE_PER_ATTO.getOrDefault(idAtto, List.of()));
    }

    /**
     * Avanza all'atto successivo nella sequenza fissa.
     * @return true se c'è un atto successivo, false se il gioco è finito
     */
    public boolean prossimoAtto() {
        if (indiceAtto + 1 >= SEQUENZA_ATTI.size()) {
            return false;
        }

        indiceAtto++;
        String idAtto = SEQUENZA_ATTI.get(indiceAtto);
        cambiaScena(idAtto);
        dialogManager.startDialogo(((Atto) dialogManager.getAtto()).getDialogoIniziale());
        return true;
    }

    /**
     * Imposta un flag prodotto da un minigioco.
     * I flag sono oggetti tecnici presenti nel DB.
     */
    public void impostaFlag(String idFlag) {
        interazioneObserver.impostaFlag(idFlag);
    }

    public void salvaPartita(int idSlot) throws SQLException {
        saveManager.salva(this.gameState, idSlot);
    }

    public void caricaPartita(int idSlot) throws SQLException {
        StatoGioco salvato = (StatoGioco) saveManager.carica(idSlot);
        if (salvato == null) return;

        cambiaScena(salvato.getIdAttoCorrente());
        dialogManager.startDialogo(salvato.getIdDialogoCorrente());

        gameState.getInventario().pulisci();
        salvato.getInventario().oggetti().forEach(gameState.getInventario()::aggiungi);
        inventarioManager.ripristina(salvato.getInventario());

        gameState.pulisciPuzzleRisolti();
        salvato.getPuzzleRisolti().forEach(gameState::aggiungiPuzzleRisolto);

        gameState.pulisciScelteEffettuate();
        salvato.getScelteEffettuate().forEach(gameState::aggiungiSceltaEffettuata);

        gameState.pulisciPassiQuestCompletati();
        salvato.getPassiQuestCompletati().forEach(gameState::aggiungiQuestCompletata);
    }

    public InterazioneObserver getInterazioneObserver() {
        return interazioneObserver;
    }

    public Map<String, Quest> getQuest() {
        return Collections.unmodifiableMap(quest);
    }

    @Override
    public void start() {
        // Carica il primo atto
        cambiaScena("a0");

        isRunning = true;
        //L'id dialogo corrente deve corrispondere al dialogo iniziale
        dialogManager.startDialogo(this.gameState.getIdDialogoCorrente());
    }

    @Override
    public void stop() {
        isRunning = false;
        wikiServer.ferma();
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
        wikiServer.avvia();
        dialogManager.init();
        inventarioManager.init();
        puzzleManager.init();
        saveManager.init();
        interazioneObserver.init();
        this.quest.putAll(new QuestLoader().load("quests/quest.json"));
    }

    @Override
    public void reset() {
        dbManager.reset();
        dialogManager.reset();
        inventarioManager.reset();
        puzzleManager.reset();
        saveManager.reset();
        interazioneObserver.reset();
    }
}
