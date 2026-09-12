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
import game.minigioco.MontacarichiManager;
import game.minigioco.ZuppaFogliantiManager;
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

    private final ZuppaFogliantiManager zuppaManager;
    private final MontacarichiManager montacarichiManager;
    private static final String ID_MONTACARICHI_COMPLETATO = "int_miniera_montacarichi";

    private static final Map<String, String> FLAG_PRECONDIZIONE_MINIGIOCO = Map.of(
            ID_MONTACARICHI_COMPLETATO, "f4"
            // futuri minigiochi...
    );

    public GameManager() {
        this.dbManager = new DBManager("config.properties");
        this.wikiServer = new WikiServer(dbManager, PORTA_WIKI);
        this.quest = new LinkedHashMap<>();

        OggettoDAO oggettoDAO = new OggettoDAO(dbManager);
        RicettaDAO ricettaDAO = new RicettaDAO(dbManager);

        this.inventarioManager = new InventarioManager(oggettoDAO, ricettaDAO);
        this.dialogManager = new DialogManager();

        zuppaManager = new ZuppaFogliantiManager(inventarioManager, ricettaDAO);
        montacarichiManager = new MontacarichiManager();

        this.interazioneObserver = new InterazioneObserver(
                (InventarioManager) inventarioManager,
                (DialogManager) dialogManager,
                this.quest
        );

        this.saveManager = new SaveManager(new StatoGiocoDAO(dbManager, oggettoDAO));

        this.gameState = new StatoGioco(
                null, null,
                new ArrayList<>(), new ArrayList<>(),
                new Inventario(), new ArrayList<>()
        );

        ((DialogManager) dialogManager).addObserver(this);
        ((InventarioManager) inventarioManager).addObserver(this);
        //importante per il funzionamento del minigioco
        ((InventarioManager) inventarioManager).addObserver(zuppaManager);
        zuppaManager.addObserver(this);
        montacarichiManager.addObserver(this);
        interazioneObserver.addObserver(this);
    }

    public void collegaGUI(GameUIListener listener) {
        GUIObserver guiObserver = new GUIObserver(listener);
        ((DialogManager) dialogManager).addObserver(guiObserver);
        ((InventarioManager) inventarioManager).addObserver(guiObserver);
        zuppaManager.addObserver(guiObserver);
        montacarichiManager.addObserver(guiObserver);
        interazioneObserver.addObserver(guiObserver);
    }

    public StatoGioco getGameState() { return gameState; }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case ATTO_CAMBIATO      -> gameState.setIdAttoCorrente((String) evento.getPayload());
            case SCELTA_EFFETTUATA  -> gameState.aggiungiSceltaEffettuata((SceltaEffettuata) evento.getPayload());
            case OGGETTO_AGGIUNTO   -> gameState.getInventario().aggiungi((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO    -> {
                BaseOggetto oggetto = (BaseOggetto) evento.getPayload();
                if (oggetto != null) gameState.getInventario().rimuovi(oggetto.getId());
            }
            case DIALOGO_CAMBIATO -> {
                BaseDialogo dialogo = (BaseDialogo) evento.getPayload();
                gameState.setIdDialogoCorrente(dialogo == null ? null : dialogo.getId());
            }
            case QUEST_COMPLETATA -> gameState.aggiungiQuestCompletata((PassoQuestCompletato) evento.getPayload());
            // Punto unico: qualunque minigioco notifichi il proprio id interazione
            // questo lo fa scattare come una normale interazione di zona
            // (che a sua volta gestisce condizioni, effetti e completamento quest).
            case MINIGIOCO_COMPLETATO -> {
                String idInterazione = (String) evento.getPayload();
                String flagPrecondizione = FLAG_PRECONDIZIONE_MINIGIOCO.get(idInterazione);
                if (flagPrecondizione != null) {
                    interazioneObserver.impostaFlag(flagPrecondizione);
                }
                interazioneObserver.tentaInterazione(idInterazione);
            }
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
        interazioneObserver.caricaZone(ZONE_PER_ATTO.getOrDefault(idAtto, List.of()));

        DialogLoader loader = new DialogLoader();
        BaseAtto<Dialogo> atto = loader.load("dialogs/" + idAtto + ".json");
        ((DialogManager) dialogManager).setAtto(atto);
    }

    public boolean prossimoAtto() {
        if (indiceAtto + 1 >= SEQUENZA_ATTI.size()) return false;
        indiceAtto++;
        String idAtto = SEQUENZA_ATTI.get(indiceAtto);
        cambiaScena(idAtto);
        dialogManager.startDialogo(((Atto) dialogManager.getAtto()).getDialogoIniziale());
        return true;
    }

    public void impostaFlag(String idFlag) {
        interazioneObserver.impostaFlag(idFlag);
    }

    // ==================== Zuppa Foglianti ====================

    public void avviaMinigiocoZuppa() {
        zuppaManager.avviaMinigioco();
    }

    public BaseOggetto combinaOggetti(List<String> idIngredienti) {
        return ((InventarioManager) inventarioManager).combina(idIngredienti);
    }

    public void selezionaErba(String idErba) {
        zuppaManager.onErbaSelezionata(idErba);
    }

    // ==================== Montacarichi ====================

    public void avviaMinigiocoMontacarichi() {
        montacarichiManager.avviaMinigioco();
    }

    public void colpisciMontacarichi() {
        montacarichiManager.onColpisci();
    }

    public void selezionaNodoMontacarichi(int indice) {
        montacarichiManager.onNodoCliccato(indice);
    }

    public MontacarichiManager getMontacarichiManager() {
        return montacarichiManager;
    }

    // ==================== Salvataggi ====================

    public void salvaPartita(int idSlot) throws SQLException {
        saveManager.salva(this.gameState, idSlot);
    }

    public void caricaPartita(int idSlot) throws SQLException {
        StatoGioco salvato = (StatoGioco) saveManager.carica(idSlot);
        if (salvato == null) {
            System.out.println("Lo slot: " + idSlot + " non esiste.");
            return;
        }

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
        cambiaScena("a1");

        inventarioManager.aggiungiOggettoDaId("o19");
        inventarioManager.aggiungiOggettoDaId("o6");
        inventarioManager.aggiungiOggettoDaId("o27");
        inventarioManager.aggiungiOggettoDaId("o28");

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
    public boolean isRunning() { return isRunning; }

    @Override
    public void init() {
        dbManager.init();
        interazioneObserver.init();
        dialogManager.init();
        saveManager.init();
        inventarioManager.init();
        this.quest.putAll(new QuestLoader().load("quests/quest.json"));
        wikiServer.avvia();
    }

    @Override
    public void reset() {
        dbManager.reset();
        dialogManager.reset();
        inventarioManager.reset();
        saveManager.reset();
        interazioneObserver.reset();
        montacarichiManager.reset();
    }
}