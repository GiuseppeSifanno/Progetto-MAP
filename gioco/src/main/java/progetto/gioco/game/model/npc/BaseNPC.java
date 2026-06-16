package progetto.gioco.game.model.npc;

import progetto.gioco.engine.model.BaseEntity;
import progetto.gioco.game.model.giocatore.Giocatore;

public abstract class BaseNPC extends BaseEntity {
    protected String idDialogoAssociato;
    protected String nome;
    protected String immagine;

    public BaseNPC(String id, String nome, String idDialogoAssociato, String immagine) {
        super(id);
        this.nome = nome;
        this.idDialogoAssociato = idDialogoAssociato;
        this.immagine = immagine;
    }

    public String getIdDialogo(){
        return this.idDialogoAssociato;
    }

    public String getImmagine(){
        return this.immagine;
    }

    public void interagisci(Giocatore giocatore){
        
    }
}
