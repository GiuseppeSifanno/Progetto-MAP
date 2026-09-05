package game.model;

import java.util.List;
import java.util.Set;

/**
 * Ricetta di combinazione: un numero qualsiasi di ingredienti (>=2) produce
 * un risultato. La corrispondenza è per insieme (l'ordine di selezione degli
 * ingredienti non conta), quindi non gestisce ingredienti ripetuti nella
 * stessa ricetta.
 * @param idRicetta id della ricetta
 * @param ingredienti lista degli id ingrediente richiesti
 * @param idRisultato id dell'oggetto risultante
 */
public record Ricetta(String idRicetta, List<String> ingredienti, String idRisultato) {

    public String getIdRicetta() {
        return idRicetta;
    }

    public List<String> getIngredienti() {
        return ingredienti;
    }

    public String getIdRisultato() {
        return idRisultato;
    }

    /**
     * Verifica se questa ricetta corrisponde esattamente all'insieme di
     * ingredienti forniti (stesso numero di elementi, stesso insieme di id,
     * indipendentemente dall'ordine).
     * @param idsForniti id degli ingredienti selezionati dal giocatore
     * @return true se corrispondono esattamente
     */
    public boolean matches(List<String> idsForniti) {
        if (idsForniti == null || idsForniti.size() != ingredienti.size()) {
            return false;
        }
        return Set.copyOf(ingredienti).equals(Set.copyOf(idsForniti));
    }
}