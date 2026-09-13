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

    /**
     * Crea una nuova istanza di StatoGioco.
     * @param idAttoCorrente identificativo univoco dell'atto corrente
     * @param idDialogoCorrente identificativo univoco del dialogo corrente
     * @param scelteEffettuate elenco delle scelte effettuate
     * @param passiQuestCompletati elenco dei passi di quest completati
     * @param inventario inventario del giocatore
     * @implNote Questo costruttore crea una nuova istanza di StatoGioco con gli attributi specificati.
     */
    public StatoGioco(String idAttoCorrente, String idDialogoCorrente, List<SceltaEffettuata> scelteEffettuate, List<PassoQuestCompletato> passiQuestCompletati,
                      Inventario inventario) {
        this.idAttoCorrente = idAttoCorrente;
        this.idDialogoCorrente = idDialogoCorrente;
        this.scelteEffettuate = scelteEffettuate;
        this.passiQuestCompletati = passiQuestCompletati;
        this.inventario = inventario;
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
}
