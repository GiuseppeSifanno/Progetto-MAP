package engine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public record Inventario(List<BaseOggetto> oggetti) {
    /**
     * Assegna una lista già esistente di oggetti
     * @param oggetti Lista di oggetti
     */
    public Inventario {}

    /**
     * Crea una lista di oggetti vuota
     */
    public Inventario() {
        this(new ArrayList<>());
    }

    /**
     * @param id id oggetto da cercare
     * @return Predicate<BaseOggetto>
     */
    private Predicate<BaseOggetto> matchesId(String id) {
        return t -> t.getId().equalsIgnoreCase(id);
    }

    /**
     * Aggiunge un oggetto allo stato corrente
     * @param oggetto BaseOggetto
     */
    public void aggiungi(BaseOggetto oggetto) {
        this.oggetti.add(oggetto);
    }

    /**
     * Rimuove un oggetto dallo stato corrente
     * @param id id oggetto da rimuovere
     */
    public void rimuovi(String id) {
        this.oggetti.removeIf(matchesId(id));
    }

    /**
     * Restituisce una lista di oggetti
     * @return List<BaseOggetto>
     */
    @Override
    public List<BaseOggetto> oggetti() {
        return Collections.unmodifiableList(this.oggetti);
    }

    /**
     * Controlla se l'inventario contiene un oggetto con l'id specificato
     * @param id id oggetto da cercare
     * @return boolean
     */
    public boolean hasOggetto(String id) {
        return this.oggetti.stream()
                .anyMatch(matchesId(id));
    }

    /**
     * Restituisce un oggetto dall'inventario
     * @param id id oggetto da cercare
     * @return BaseOggetto
     */
    public BaseOggetto getOggetto(String id) {
        return this.oggetti.stream()
                .filter(matchesId(id))
                .findFirst()
                .orElse(null);
    }

    public void pulisci() {
        this.oggetti.clear();
    }
}
