package progetto.gioco.model;

public class AbstractEntity {
    private String id;
    private String nome;
    private final String dialogoIniziale;

    public AbstractEntity(String dialogoIniziale, String nome) {
        this.dialogoIniziale = dialogoIniziale;
        this.nome = nome;
    }
    public String getDialogoIniziale() {
        return dialogoIniziale;
    }

    //restituisce il nome dell'NPC
    @Override
    public String toString() {
        return nome;
    }
}
