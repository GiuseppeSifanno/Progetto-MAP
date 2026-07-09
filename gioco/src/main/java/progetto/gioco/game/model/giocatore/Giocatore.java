package progetto.gioco.game.model.giocatore;

import progetto.gioco.engine.model.BaseEntity;
import progetto.gioco.game.model.Inventario;
import progetto.gioco.game.model.npc.BaseNPC;

//TODO considerare la rimozione di inventario poichè è gestito direttamente dal manger
public class Giocatore extends BaseEntity {
    private final String nome;
    private final Inventario inventario;

    public Giocatore(String id, String nome, Inventario inventario) {
        super(id);
        this.nome = nome;
        this.inventario = inventario;
    }

    /** 
     * @return String
     */
    public String getNome() {
        return nome;
    }

    /** 
     * @return Inventario
     */
    public Inventario getInventario(){
        return this.inventario;
    }

    /** 
     * @param npc NPC da interagire con
     */
    public void interagisci(BaseNPC npc){
        npc.interagisci(this);
    }
}
