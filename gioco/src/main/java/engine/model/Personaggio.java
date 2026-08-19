package engine.model;

public class Personaggio extends BaseEntity {
    protected String nome;

    public Personaggio(String id, String nome) {
        super(id);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
