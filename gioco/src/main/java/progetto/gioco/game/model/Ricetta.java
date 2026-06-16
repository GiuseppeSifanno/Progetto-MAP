package progetto.gioco.game.model;

public class Ricetta {
    private String idIngrediente1;
    private String idIngrediente2;
    private String idRisultato;

    public Ricetta(String idIngrediente1, String idIngrediente2, String idRisultato) {
        this.idIngrediente1 = idIngrediente1;
        this.idIngrediente2 = idIngrediente2;
        this.idRisultato = idRisultato;
    }

    /**
     * @return String
     */
    public String getIdIngrediente1() {
        return idIngrediente1;
    }

    /**
     * @return String
     */
    public String getIdIngrediente2() {
        return idIngrediente2;
    }

    /**
     * @return String
     */
    public String getIdRisultato() {
        return idRisultato;
    }

    public boolean matches(String id1, String id2) {
        return (idIngrediente1.equalsIgnoreCase(id1) && idIngrediente2.equalsIgnoreCase(id2)) ||
            (idIngrediente1.equalsIgnoreCase(id2) && idIngrediente2.equalsIgnoreCase(id1));
    }
}
