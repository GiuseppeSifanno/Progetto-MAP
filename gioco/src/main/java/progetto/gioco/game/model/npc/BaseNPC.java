package progetto.gioco.game.model.npc;

import progetto.gioco.engine.model.BaseEntity;
import progetto.gioco.game.model.giocatore.Giocatore;

public abstract class BaseNPC extends BaseEntity {
    protected final String idDialogoAssociato;
    protected final String nome;
    protected final String immagine;

    public BaseNPC(String id, String nome, String idDialogoAssociato, String immagine) {
        super(id);
        this.nome = nome;
        this.idDialogoAssociato = idDialogoAssociato;
        this.immagine = immagine;
    }

    /** 
     * @return String
     */
    public String getIdDialogo(){
        return this.idDialogoAssociato;
    }

    /** 
     * @return String
     */
    public String getImmagine(){
        return this.immagine;
    }

    /** 
     * @param giocatore
     */
    public void interagisci(Giocatore giocatore){
        
    }
}
