package progetto.gioco.controller;

import progetto.gioco.loader.DialogLoader;
import progetto.gioco.manager.DialogManager;
import progetto.gioco.model.Atto;
import progetto.gioco.model.Dialogo;
import progetto.gioco.model.NPC;
import progetto.gioco.model.Scelta;

public class GameController {
    private GameState gameState;
    private DialogManager dialogManager;
    private DialogLoader dialogLoader;

    public GameController(){
        this.gameState = new GameState();
        this.dialogManager = new DialogManager();
        this.dialogLoader = new DialogLoader();
    }

    public void caricaAtto(String path){
        Atto atto = dialogLoader.load(path);
        dialogManager.setAtto(atto);
    }

    public void interagisci(NPC npc){
        //recupero il dialogo per un certo contesto di gioco
        String idDialogo = npc.getDialogo(gameState);
        if (idDialogo.isEmpty()) return;
        //fa partire il dialogo con un certo id
        dialogManager.startDialogo(idDialogo);
    }

    public Scelta scegliOpzione(int scelta) {
        Scelta s = dialogManager.scegliOpzione(scelta);
        /*
        * In questo punto possono essere inseriti altri controlli per gestire lo stato del gioco
        */
        return s;
    }

    public Dialogo getDialogoCorrente() {
        //restituisce il dialogo corrente
        return dialogManager.getDialogo();
    }
}
