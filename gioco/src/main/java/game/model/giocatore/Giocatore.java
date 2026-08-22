package game.model.giocatore;

import engine.model.BaseEntity;

public class Giocatore extends BaseEntity {
    private final String nome;

    public Giocatore(String id, String nome) {
        super(id);
        this.nome = nome;
    }

    /** 
     * @return String
     */
    public String getNome() {
        return nome;
    }
}
