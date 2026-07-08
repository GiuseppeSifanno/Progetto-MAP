package progetto.gioco.engine.model;

/**
 * Classe astratta che rappresenta un oggetto.
 */
public abstract class BaseOggetto extends BaseEntity {
    protected final String nome;
    protected final String descrizione;
    protected final String filename;

    /**
     * Costruttore di base.
     * @param id Id oggetto
     * @param nome Nome oggetto
     * @param descrizione Descrizione oggetto
     * @param filename Posizione assets dell'oggetto
     * @implNote il <i>filename</i> si riferisce alla posizione relativa all'asset grafico dell'oggetto.
     * Potrebbe essere rimosso se in conflitto con la GUI
     */
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

    /**
     * Usa un oggetto.
     * @implNote Ogni oggetto ha un comportamento diverso quando viene usato.
     */
    public abstract void usa();
}
