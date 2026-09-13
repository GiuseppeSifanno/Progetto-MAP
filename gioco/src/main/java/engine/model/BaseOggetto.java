package engine.model;

/**
 * Classe astratta che rappresenta un oggetto.
 */
public abstract class BaseOggetto extends BaseEntity {
    /** Nome dell'oggetto. */
    protected final String nome;
    /** Descrizione dell'oggetto. */
    protected final String descrizione;
    /** Posizione assets dell'oggetto. */
    protected final String filename;
    /** Indica se l'oggetto è combinabile. */
    protected final boolean combinabile;

    /**
     * Costruttore di base.
     * @param id Id oggetto
     * @param nome Nome oggetto
     * @param descrizione Descrizione oggetto
     * @param filename Posizione assets dell'oggetto
     * @param combinabile Indica se l'oggetto è combinabile
     * @implNote il <i>filename</i> si riferisce alla posizione relativa all'asset grafico dell'oggetto.
     * Potrebbe essere rimosso se in conflitto con la GUI
     *
     */
    public BaseOggetto(String id, String nome, String descrizione, String filename, boolean combinabile) {
        super(id);
        this.nome = nome;
        this.descrizione = descrizione;
        this.filename = filename;
        this.combinabile = combinabile;
    }

    /**
     * Ritorna il nome dell'oggetto.
     * @return String
     */
    public String getNome(){
        return this.nome;
    }

    /**
     * Ritorna la descrizione dell'oggetto.
     * @return String
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Ritorna il filename dell'oggetto.
     * @return String
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Ritorna se l'oggetto è combinabile.
     * @return boolean
     */
    public boolean getCombinabile() { return combinabile; }
}
