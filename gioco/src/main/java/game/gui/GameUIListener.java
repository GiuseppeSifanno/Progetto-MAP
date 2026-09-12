package game.gui;

import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.minigioco.MontacarichiManager;
import game.minigioco.ZuppaFogliantiManager;
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
     * Metodo che viene chiamato quando viene mostrato un messaggio.
     * @param messaggio Messaggio da mostrare
     */
    void onMessaggioMostrato(String messaggio);

    /**
     * Metodo che viene chiamato quando viene completata una passata della quest.
     * @param passo Passata completata
     */
    void onQuestCompletata(PassoQuestCompletato passo);


    // ==================== Zuppa Foglianti ====================
    void onMinigiocoAvviato();

    void onMinigiocoErbaEsito(ZuppaFogliantiManager.EsitoErba esito);

    // ==================== Montacarichi ====================
    void onMinigiocoFaseCambiataMontacarichi(MontacarichiManager.Fase fase);

    void onMinigiocoIndicatoreAggiornato(int posizione);

    void onMinigiocoColpoEsito(MontacarichiManager.EsitoColpo esito);

    void onMinigiocoNodoEsito(MontacarichiManager.EsitoNodo esito);

    // ==================== Comune ====================
    /** Un minigioco è stato completato. idPayload identifica quale (id interazione sintetica). */
    void onMinigiocoCompletato(String idPayload);
}