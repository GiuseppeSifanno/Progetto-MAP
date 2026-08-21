package game.model.npc;

import java.util.List;

import engine.observer.GameEvent;
import engine.observer.GameObserver;
import game.model.giocatore.Giocatore;
import game.model.oggetti.Materiale;

public class NPC_Raccoglitore extends BaseNPC {
    private Materiale materialeRaccolto;
    private int quantitaPerCiclo; 
    private int intervalloSecondi;
    private List<GameObserver> observers;

    public NPC_Raccoglitore(String id, String nome, String idDialogoAssociato, String immagine) {
        super(id, nome, idDialogoAssociato, immagine);
    }

    /** 
     * @param giocatore
     */
    public void interagisci(Giocatore giocatore){

    }

    public void avviaRaccolta(){

    }

    public void fermaRaccolta(){

    }

    /** 
     * @param o
     */
    public void addObserver(GameObserver o){
        observers.add(o);
    }

    /** 
     * @param e
     */
    public void notifyObservers(GameEvent e){
    }
}
