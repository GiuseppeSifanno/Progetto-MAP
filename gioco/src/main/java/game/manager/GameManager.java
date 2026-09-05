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
import game.minigioco.ZuppaFogliantiManager;
import game.model.*;
import game.model.minigioco.Erba;
import game.model.minigioco.ZuppaFogliantiConfig;
import game.observer.GUIObserver;
import game.observer.InterazioneObserver;
import game.gui.GameUIListener;
import game.rest.WikiServer;

import java.sql.SQLException;
import java.util.*;

/**
 * Classe concreta del GameManager che gestisce l'interazione con il gioco.
 * @author Giuseppe
 */
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
    private ZuppaFogliantiManager zuppaManager;
    final ZuppaFogliantiConfig configZuppa = new ZuppaFogliantiConfig(
            List.of(
                    new Erba("o20", "Fiori Gialli", true),
                    new Erba("o21", "Fiori Viola", true),
                    new Erba("o22", "Fiori Azzurri", true),
                    new Erba("o23", "Bacche Rosse", true),
                    new Erba("o24", "Funghi Chiazzati", false),
                    new Erba("o25", "Radice Contorta", false),
                    new Erba("o26", "Radice Nodosa", false)
            ),
            4,      // erbeCorretteRichieste (su 7 totali)
            40,     // zonaVerdeMin
            60,     // zonaVerdeMax
            3,      // colpiRichiesti
            50,     // velocitaIndicatoreMs (più lento del default, per testare a mano)
            "o19",  // id oggetto tazza da tè (assumendo l'abbiate inserito così a DB)
            "o12"   // oggetto zuppa
    );

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

        zuppaManager = new ZuppaFogliantiManager(configZuppa, inventarioManager, dialogManager);

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
        zuppaManager.addObserver(this);
        interazioneObserver.addObserver(this);
    }

    public void collegaGUI(GameUIListener listener) {
        GUIObserver guiObserver = new GUIObserver(listener);
        ((DialogManager) dialogManager).addObserver(guiObserver);
        ((InventarioManager) inventarioManager).addObserver(guiObserver);
        ((PuzzleManager) puzzleManager).addObserver(guiObserver);
        zuppaManager.addObserver(guiObserver);
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

    /**
     * Avvia il minigioco della zuppa dei Foglianti (Atto 2), chiamato dalla GUI
     * quando il giocatore preme "Inizia minigioco".
     */
    public void avviaMinigiocoZuppa() {
        zuppaManager.avviaMinigioco();
    }

    /**
     * Passthrough dalla GUI: combina una lista di ingredienti (bottone
     * "Combina" nell'inventario). Se il risultato è proprio la zuppa dei
     * Foglianti, notifica anche il completamento del minigioco per riusare
     * la stessa scena finale (zuppa.png + transizione) già collegata.
     */
    public BaseOggetto combinaOggetti(List<String> idIngredienti) {
        BaseOggetto risultato = ((InventarioManager) inventarioManager).combina(idIngredienti);
        if (risultato != null && configZuppa.idOggettoRisultato().equals(risultato.getId())) {
            zuppaManager.notificaCompletatoDaCombinazione();
        }
        return risultato;
    }

    /** Passthrough dalla GUI: il giocatore ha cliccato un'erba/radice nella fase Navigatrice. */
    public void selezionaErba(String idErba) {
        zuppaManager.onErbaSelezionata(idErba);
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
        inventarioManager.aggiungiOggettoDaId("o2");
        cambiaScena("a2");
        isRunning = true;
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
        interazioneObserver.init();
        dialogManager.init();
        saveManager.init();
        inventarioManager.init();
        this.quest.putAll(new QuestLoader().load("quests/quest.json"));
        puzzleManager.init();
        //lasciamo che si avvi per ultimo
        wikiServer.avvia();
    }

    @Override
    public void reset() {
        dbManager.reset();
        dialogManager.reset();
        inventarioManager.reset();
        puzzleManager.reset();
        saveManager.reset();
        interazioneObserver.reset();
        zuppaManager.reset();
    }
}