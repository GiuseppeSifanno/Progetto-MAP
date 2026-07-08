package progetto.gioco.game.controller;

import progetto.gioco.engine.manager.Startable;
import progetto.gioco.game.loader.DialogLoader;
import progetto.gioco.game.manager.DialogManager;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Scelta;
import progetto.gioco.game.model.npc.BaseNPC;

/**
 * Classe che ha il compito di gestire tutti i controlli del gioco.
 * Può accedere allo stato di gioco.
 */
public class GameController implements Startable {
    private final GameState gameState;
    private final DialogManager dialogManager;
    private final DialogLoader dialogLoader;

    private boolean isRunning = false;

    public GameController(){
        this.gameState = new GameState();
        this.dialogManager = new DialogManager();
        this.dialogLoader = new DialogLoader();
    }

    /** 
     * @param path Percorso del file json
     */
    public void caricaAtto(String path){
        Atto atto = dialogLoader.load(path);
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

    /**
     * @param scelta Numero della scelta da fare
     * @return Scelta
     */
    public Scelta scegliOpzione(int scelta) {
        Scelta s = dialogManager.scegliOpzione(scelta);
        gameState.addScelta(s.getIdScelta());
        /*
        * In questo punto possono essere inseriti altri controlli per gestire lo stato del gioco
        */
        return s;
    }

    /** 
     * @return Dialogo
     */
    public Dialogo getDialogoCorrente() {
        //restituisce il dialogo corrente
        return dialogManager.getDialogo();
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
