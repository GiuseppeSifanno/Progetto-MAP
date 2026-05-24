package progetto.gioco.engine.model;

public abstract class BaseOggetto {
    protected String id;
    protected String nome;

    /** 
     * @return String
     */
    public String getNome(){
        return this.nome;
    }

    public abstract void usa();
}
