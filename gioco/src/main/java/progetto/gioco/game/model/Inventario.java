package progetto.gioco.game.model;

import java.util.List;
import java.util.function.Predicate;

import progetto.gioco.engine.model.BaseOggetto;

public class Inventario {
    private List<BaseOggetto> oggetti;

    public Inventario(List<BaseOggetto> oggetti) {
        this.oggetti = oggetti;
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
    public void aggiungi(BaseOggetto oggetto){
        this.oggetti.add(oggetto);
    }

    /** 
     * @param id
     */
    public void rimuovi(String id){
        this.oggetti.removeIf(matchesId(id));
    }

    /** 
     * @return List<BaseOggetto>
     */
    public List<BaseOggetto> getOggetti(){
        return this.oggetti;
    }

    /** 
     * @param id
     * @return boolean
     */
    public boolean hasOggetto(String id){
        return this.oggetti.stream()
                .anyMatch(matchesId(id));
    }

    /** 
     * @param id
     * @return BaseOggetto
     */
    public BaseOggetto getOggetto(String id){
        return this.oggetti.stream()
                .filter(matchesId(id))
                .findFirst()
                .orElse(null);
    }
}
