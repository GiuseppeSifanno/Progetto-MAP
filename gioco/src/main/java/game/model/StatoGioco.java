package game.model;

import java.util.List;

public class StatoGioco {
    private String idAttoCorrente;
    private String idDialogoCorrente;
    private final List<SceltaEffettuata> scelteEffettuate;
    private final List<PassoQuestCompletato> passiQuestCompletati;
    private final Inventario inventario;
    private final List<String> puzzleRisolti;

    public StatoGioco(String idAttoCorrente, String idDialogoCorrente, List<SceltaEffettuata> scelteEffettuate, List<PassoQuestCompletato> passiQuestCompletati,
                      Inventario inventario, List<String> puzzleRisolti) {
        this.idAttoCorrente = idAttoCorrente;
        this.idDialogoCorrente = idDialogoCorrente;
        this.scelteEffettuate = scelteEffettuate;
        this.passiQuestCompletati = passiQuestCompletati;
        this.inventario = inventario;
        this.puzzleRisolti = puzzleRisolti;
    }

    /**
     * @return id atto corrente
     */
    public String getIdAttoCorrente() {
        return idAttoCorrente;
    }

    /**
     * @param idAttoCorrente id atto corrente
     */
    public void setIdAttoCorrente(String idAttoCorrente) {
        this.idAttoCorrente = idAttoCorrente;
    }

    /**
     * @return Id dialogo corrente
     */
    public String getIdDialogoCorrente() {
        return idDialogoCorrente;
    }

    /**
     * @param idDialogoCorrente Id dialogo corrente
     */
    public void setIdDialogoCorrente(String idDialogoCorrente) {
        this.idDialogoCorrente = idDialogoCorrente;
    }

    /**
     * @return Lista di scelte effettuate
     */
    public List<SceltaEffettuata> getScelteEffettuate() {
        return scelteEffettuate;
    }

    /**
     * @return Ritorna la lista dei passaggi delle questi completati
     */
    public List<PassoQuestCompletato> getPassiQuestCompletati() {
        return passiQuestCompletati;
    }

    public void aggiungiQuestCompletata(PassoQuestCompletato quest) {
        this.passiQuestCompletati.add(quest);
    }

    /**
     * @return Inventario
     */
    public Inventario getInventario() {
        return inventario;
    }

    /**
     * @return Lista di puzzle risolti
     */
    public List<String> getPuzzleRisolti() {
        return puzzleRisolti;
    }
}
