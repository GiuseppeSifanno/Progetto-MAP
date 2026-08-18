package game.model;

public record Ricetta(String idRicetta, String idIngrediente1, String idIngrediente2, String idRisultato) {

    /**
     * Restituisce l'id della ricetta
     * @return id della ricetta
     */
    public String getIdRicetta() {
        return idRicetta;
    }

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
     * Verifica se questa ricetta corrisponde alla coppia di ingredienti forniti,
     * indipendentemente dall'ordine.
     * @param id1 id del primo ingrediente
     * @param id2 id del secondo ingrediente
     * @return true se la ricetta usa esattamente questi due ingredienti, false altrimenti
     */
    public boolean matches(String id1, String id2) {
        return (idIngrediente1.equalsIgnoreCase(id1) && idIngrediente2.equalsIgnoreCase(id2)) ||
                (idIngrediente1.equalsIgnoreCase(id2) && idIngrediente2.equalsIgnoreCase(id1));
    }
}
