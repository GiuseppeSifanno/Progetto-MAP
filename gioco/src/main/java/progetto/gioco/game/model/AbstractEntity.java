package progetto.gioco.game.model;

public class AbstractEntity {
    private String id;
    private String nome;
    private final String dialogoIniziale;

    public AbstractEntity(String dialogoIniziale, String nome) {
        this.dialogoIniziale = dialogoIniziale;
        this.nome = nome;
    }
    /** 
     * @return String
     */
    public String getDialogoIniziale() {
        return dialogoIniziale;
    }

    /** 
     * @return String
     */
    //restituisce il nome dell'NPC
    @Override
    public String toString() {
        return nome;
    }
}
