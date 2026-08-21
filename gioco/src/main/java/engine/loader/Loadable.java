package engine.loader;

/**
 * Interfaccia che definisce un metodo per la lettura di un file.
 * @param <T> tipo generico per un oggetto
 */
public interface Loadable<T> {
    /**
     * Carica un oggetto dal file.
     * @param path Percorso relativo al file da caricare
     * @return Oggetto caricato
     */
    T load(String path);
}
