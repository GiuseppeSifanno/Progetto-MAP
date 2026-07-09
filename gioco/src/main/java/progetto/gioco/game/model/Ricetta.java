package progetto.gioco.game.model;

public record Ricetta(String idRicetta, String idIngrediente1, String idIngrediente2, String idRisultato) {
    /**
     * @return id ingrediente 1
     */
    public String getIdIngrediente1() {
        return idIngrediente1;
    }

    /**
     * @return id ingrediente 2
     */
    public String getIdIngrediente2() {
        return idIngrediente2;
    }

    /**
     * @return id ricetta risultato
     */
    public String getIdRisultato() {
        return idRisultato;
    }

    /**
     * @param id1 id ingrediente 1
     * @param id2 id ingrediente 2
     * @return true se i due ingredienti sono uguali, false altrimenti
     */
    public boolean matches(String id1, String id2) {
        return (idIngrediente1.equalsIgnoreCase(id1) && idIngrediente2.equalsIgnoreCase(id2)) ||
                (idIngrediente1.equalsIgnoreCase(id2) && idIngrediente2.equalsIgnoreCase(id1));
    }
}
