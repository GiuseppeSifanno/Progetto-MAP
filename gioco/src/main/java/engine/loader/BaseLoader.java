package engine.loader;

/**
 * Classe astratta che gestisce il caricamento dei dati.
 * @param <T> tipo generico per un oggetto
 */
public abstract class BaseLoader<T> {
    /**
     * Carica un oggetto dal file.
     * @param path Percorso relativo al file da caricare
     * @return Oggetto caricato
     */
    public abstract T load(String path);

    /**
     * Converti un oggetto DTO in un oggetto.
     * @param dto oggetto DTO da convertire
     * @return Oggetto convertito
     */
    protected abstract T convert(Object dto);
}
