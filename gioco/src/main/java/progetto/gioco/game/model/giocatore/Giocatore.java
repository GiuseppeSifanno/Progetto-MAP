package progetto.gioco.game.model.giocatore;

import progetto.gioco.engine.model.BaseEntity;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.game.model.Inventario;
import progetto.gioco.game.model.npc.BaseNPC;

public class Giocatore extends BaseEntity {
    private String nome;
    private Inventario inventario;

    public Giocatore(String id, String nome, Inventario inventario) {
        super(id);
        this.nome = nome;
        this.inventario = inventario;
    }

    public String getNome() {
        return nome;
    }

    public Inventario getInventario(){
        return this.inventario;
    }

    public void interagisci(BaseNPC npc){
        npc.interagisci(this);
    }

    public void onEvent(GameEvent evento){
        
    }
}
