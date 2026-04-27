package progetto.gioco.model;

public class AbstractEntity {
    private String id;
    private String nome;
    private final String dialogoIniziale;

    public AbstractEntity(String dialogoIniziale) {
        this.dialogoIniziale = dialogoIniziale;
    }
}
