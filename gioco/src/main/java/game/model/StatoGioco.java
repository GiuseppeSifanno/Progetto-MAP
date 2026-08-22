package game.model;

import engine.model.Inventario;

import java.util.Collections;
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
     * @return Lista di scelte effettuate <b>non modificabile</b>
     */
    public List<SceltaEffettuata> getScelteEffettuate() {
        return Collections.unmodifiableList(scelteEffettuate);
    }

    /**
     * Aggiunge una scelta effettuata.
     * @param scelta Scelta effettuata
     */
    public void aggiungiSceltaEffettuata(SceltaEffettuata scelta) {
        this.scelteEffettuate.add(scelta);
    }

    /**
     * Pulisce le scelte effettuate.
     */
    public void pulisciScelteEffettuate() {
        this.scelteEffettuate.clear();
    }

    /**
     * @return Ritorna la lista dei passaggi delle questi completati <b>non modificabile</b>
     */
    public List<PassoQuestCompletato> getPassiQuestCompletati() {
        return Collections.unmodifiableList(passiQuestCompletati);
    }

    /**
     * Aggiunge un passaggio della quest completata.
     * @param quest Passaggio della quest completata
     */
    public void aggiungiQuestCompletata(PassoQuestCompletato quest) {
        this.passiQuestCompletati.add(quest);
    }

    /** Pulisce la lista dei passaggi delle questi completati */
    public void pulisciPassiQuestCompletati() {
        this.passiQuestCompletati.clear();
    }

    /**
     * @return Inventario
     */
    public Inventario getInventario() {
        return inventario;
    }

    /**
     * @return Lista di puzzle risolti <b>non modificabile</b>
     */
    public List<String> getPuzzleRisolti() {
        return Collections.unmodifiableList(puzzleRisolti);
    }

    /**
     * Aggiunge un puzzle risolto.
     * @param idPuzzle Id puzzle risolto
     */
    public void aggiungiPuzzleRisolto(String idPuzzle) {
        this.puzzleRisolti.add(idPuzzle);
    }

    /** Pulisce la lista dei puzzle risolti */
    public void pulisciPuzzleRisolti() { this.puzzleRisolti.clear(); }
}
