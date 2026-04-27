package progetto.gioco.model;

public class NPC extends AbstractEntity{
    public NPC(String dialogoIniziale) {
        super(dialogoIniziale);
    }

    public String getDialogo(GameState state){
        //in base allo stato del gioco (quindi decisioni ecc...) si restituisce il giusto id
        //per far proseguire la storia
        return "";
    }
}
