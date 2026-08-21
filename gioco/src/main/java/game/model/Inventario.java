package game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import engine.model.BaseOggetto;

public record Inventario(List<BaseOggetto> oggetti) {
    /**
     * Assegna una lista già esistente di oggetti
     *
     * @param oggetti
     */
    public Inventario {
    }

    /**
     * Crea una lista di oggetti vuota
     */
    public Inventario() {
        this(new ArrayList<>());
    }

    /**
     * @param id
     * @return Predicate<BaseOggetto>
     */
    private Predicate<BaseOggetto> matchesId(String id) {
        return t -> t.getId().equalsIgnoreCase(id);
    }

    /**
     * @param oggetto
     */
    public void aggiungi(BaseOggetto oggetto) {
        this.oggetti.add(oggetto);
    }

    /**
     * @param id
     */
    public void rimuovi(String id) {
        this.oggetti.removeIf(matchesId(id));
    }

    /**
     * @return List<BaseOggetto>
     */
    @Override
    public List<BaseOggetto> oggetti() {
        return Collections.unmodifiableList(this.oggetti);
    }

    /**
     * @param id
     * @return boolean
     */
    public boolean hasOggetto(String id) {
        return this.oggetti.stream()
                .anyMatch(matchesId(id));
    }

    /**
     * @param id
     * @return BaseOggetto
     */
    public BaseOggetto getOggetto(String id) {
        return this.oggetti.stream()
                .filter(matchesId(id))
                .findFirst()
                .orElse(null);
    }
}
