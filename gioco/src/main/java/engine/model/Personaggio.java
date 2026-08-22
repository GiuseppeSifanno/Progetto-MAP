package engine.model;

/**
 * Classe che rappresenta un personaggio.
 */
public class Personaggio extends BaseEntity {
    /** Nome del personaggio. */
    protected String nome;

    /**
     * Costruttore di base.
     * @param id Id personaggio
     * @param nome Nome personaggio
     */
    public Personaggio(String id, String nome) {
        super(id);
        this.nome = nome;
    }

    /**
     * @return String
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del personaggio.
     * @param nome Nome personaggio
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
}
