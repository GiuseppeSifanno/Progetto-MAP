package game.gui;

import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.model.PassoQuestCompletato;
import game.model.SceltaEffettuata;

/**
 * Interfaccia per gli eventi che possono essere gestiti dalla GUI.
 */
public interface GameUIListener {
    /**
     * Metodo che viene chiamato quando viene cambiato il dialogo.
     * @param dialogo Dialogo corrente
     */
    void onDialogoCambiato(BaseDialogo dialogo);

    /**
     * Metodo che viene chiamato quando viene effettuata una scelta.
     * @param scelta Scelta effettuata
     */
    void onSceltaEffettuata(SceltaEffettuata scelta);

    /**
     * Metodo che viene chiamato quando viene aggiunto o rimosso un oggetto.
     * @param oggetto Oggetto aggiunto o rimosso
     */
    void onOggettoAggiunto(BaseOggetto oggetto);

    /**
     * Metodo che viene chiamato quando viene rimosso un oggetto.
     * @param oggetto Oggetto rimosso
     */
    void onOggettoRimosso(BaseOggetto oggetto);

    /**
     * Metodo che viene chiamato quando viene cambiato l'atto.
     * @param idAtto Id dell'atto corrente
     */
    void onAttoCambiato(String idAtto);

    /**
     * Metodo che viene chiamato quando viene risolto un puzzle.
     * @param idPuzzle Id puzzle risolto
     */
    void onPuzzleRisolto(String idPuzzle);

    /**
     * Metodo che viene chiamato quando viene mostrato un messaggio.
     * @param messaggio Messaggio da mostrare
     */
    void onMessaggioMostrato(String messaggio);

    /**
     * Metodo che viene chiamato quando viene completata una passata della quest.
     * @param passo Passata completata
     */
    void onQuestCompletata(PassoQuestCompletato passo);
}