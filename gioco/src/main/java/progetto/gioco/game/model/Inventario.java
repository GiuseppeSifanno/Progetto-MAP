package progetto.gioco.game.model;

import java.util.List;
import java.util.function.Predicate;

import progetto.gioco.engine.model.BaseOggetto;

public class Inventario {
    private List<BaseOggetto> oggetti;

    public Inventario(List<BaseOggetto> oggetti) {
        this.oggetti = oggetti;
    }

    private Predicate<BaseOggetto> matchesId(String id) {
        return t -> t.getId().equalsIgnoreCase(id);
    }

    public void aggiungi(BaseOggetto oggetto){
        this.oggetti.add(oggetto);
    }

    public void rimuovi(String id){
        this.oggetti.removeIf(matchesId(id));
    }

    public List<BaseOggetto> getOggetti(){
        return this.oggetti;
    }

    public boolean hasOggetto(String id){
        return this.oggetti.stream()
                .anyMatch(matchesId(id));
    }

    public BaseOggetto getOggetto(String id){
        return this.oggetti.stream()
                .filter(matchesId(id))
                .findFirst()
                .orElse(null);
    }
}
