package progetto.gioco.model;

import progetto.gioco.controller.GameState;

public class NPC extends AbstractEntity{
    public NPC(String dialogoIniziale, String nome) {
        super(dialogoIniziale, nome);
    }

    public String getDialogo(GameState state){
        //in base allo stato del gioco (quindi decisioni ecc...) si restituisce il giusto id
        //per far proseguire la storia
        return getDialogoIniziale();
    }
}
