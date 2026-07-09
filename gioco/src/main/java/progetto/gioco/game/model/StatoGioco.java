package progetto.gioco.game.model;

import java.util.List;

public class StatoGioco {
    private String idAttoCorrente;
    private final List<String> scelteEffettuate;
    private final List<String> inventario;
    private final List<String> puzzleRisolti;

    public StatoGioco(String idAttoCorrente, List<String> scelteEffettuate,
                    List<String> inventario, List<String> puzzleRisolti) {
        this.idAttoCorrente = idAttoCorrente;
        this.scelteEffettuate = scelteEffettuate;
        this.inventario = inventario;
        this.puzzleRisolti = puzzleRisolti;
    }

    /**
     * @return String
     */
    public String getIdAttoCorrente() {
        return idAttoCorrente;
    }

    /**
     * @param idAttoCorrente
     */
    public void setIdAttoCorrente(String idAttoCorrente) {
        this.idAttoCorrente = idAttoCorrente;
    }

    /**
     * @return List<String>
     */
    public List<String> getScelteEffettuate() {
        return scelteEffettuate;
    }

    /**
     * @return List<String>
     */
    public List<String> getInventario() {
        return inventario;
    }

    /**
     * @return List<String>
     */
    public List<String> getPuzzleRisolti() {
        return puzzleRisolti;
    }
}
