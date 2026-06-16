package progetto.gioco.game.model.npc;

import java.util.List;

import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.game.model.giocatore.Giocatore;
import progetto.gioco.game.model.oggetti.Materiale;

public class NPC_Raccoglitore extends BaseNPC {
    private Materiale materialeRaccolto;
    private int quantitaPerCiclo; 
    private int intervalloSecondi;
    private List<GameObserver> observers;

    public NPC_Raccoglitore(String id, String nome, String idDialogoAssociato, String immagine) {
        super(id, nome, idDialogoAssociato, immagine);
    }

    public void interagisci(Giocatore giocatore){

    }

    public void avviaRaccolta(){

    }

    public void fermaRaccolta(){

    }

    public void addObserver(GameObserver o){
        observers.add(o);
    }

    public void notifyObservers(GameEvent e){
    }
}
