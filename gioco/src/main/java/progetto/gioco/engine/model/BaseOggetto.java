package progetto.gioco.engine.model;

import java.util.Objects;

public abstract class BaseOggetto extends BaseEntity{
    protected String nome;

    public BaseOggetto(String id, String nome) {
        super(id);
        this.nome = nome;
    }

    /** 
     * @return String
     */
    public String getNome(){
        return this.nome;
    }

    public abstract void usa();

    /**
     * Confronta tutti gli attributi dei due oggetti.
     * Se il numero di attributi cresce, considerare un'altra soluzione
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) 
            return false;

        if(obj.getClass() != this.getClass()) 
            return false;

        final BaseOggetto other = (BaseOggetto) obj;

        if( (this.id == null) ? (other.id != null) : !this.id.equalsIgnoreCase(other.id))
            return false;

        return Objects.equals(this.nome, other.nome);
    }
}
