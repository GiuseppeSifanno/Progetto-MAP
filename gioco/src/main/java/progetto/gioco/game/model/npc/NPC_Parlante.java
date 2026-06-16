package progetto.gioco.game.model.npc;

import progetto.gioco.game.model.giocatore.Giocatore;

public class NPC_Parlante extends BaseNPC {
    public NPC_Parlante(String id, String nome, String idDialogoAssociato, String immagine) {
        super(id, nome, idDialogoAssociato, immagine);
    }

    /** 
     * @param giocatore
     */
    public void interagisci(Giocatore giocatore){
    }
}
