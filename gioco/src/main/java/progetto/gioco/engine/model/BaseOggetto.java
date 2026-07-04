package progetto.gioco.engine.model;

import java.util.Objects;

public abstract class BaseOggetto extends BaseEntity{
    protected String nome;
    protected String descrizione;
    protected String filename;

    public BaseOggetto(String id, String nome, String descrizione, String filename) {
        super(id);
        this.nome = nome;
        this.descrizione = descrizione;
        this.filename = filename;
    }

    /** 
     * @return String
     */
    public String getNome(){
        return this.nome;
    }

    public abstract void usa();
}
