package engine.loader;

/**
 * Interfaccia che definisce un metodo per la lettura di un file.
 * @param <T> tipo generico per un oggetto
 * @param <D> tipo generico per un oggetto DTO
 * @implSpec Il tipo T è quello dell'oggetto concreto che vogliamo convertire,
 *           mentre il tipo D è quello dell'oggetto DTO che vogliamo convertire.
 *           Ad esempio il tipo Atto (come T) va in coppia con AttoDTO (come D).
 */
public interface Loadable<T, D> {
    /**
     * Carica un oggetto dal file.
     * @param path Percorso relativo al file da caricare
     * @return Oggetto caricato
     */
    T load(String path);

    /**
     * Converti un oggetto DTO in un oggetto.
     * @param dto oggetto DTO da convertire
     * @return Oggetto convertito
     */
    T convert(D dto);
}
