package progetto.gioco.engine.model;

public abstract class BaseOggetto {
    private String id;
    private String nome;

    public String getId() {
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }

    public abstract void usa();
}
