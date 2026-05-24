package progetto.gioco.engine.model;

public abstract class BaseOggetto {
    private String id;
    private String nome;

    /** 
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /** 
     * @return String
     */
    public String getNome(){
        return this.nome;
    }

    public abstract void usa();
}
