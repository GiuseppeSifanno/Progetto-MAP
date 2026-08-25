package engine.observer;

/**
 * Enumerazione che definisce i tipi di eventi del gioco.
 */
public enum TipoEvento {
    SCELTA_EFFETTUATA,
    OGGETTO_AGGIUNTO,
    OGGETTO_RIMOSSO,
    PUZZLE_RISOLTO,
    ATTO_CAMBIATO,
    DIALOGO_CAMBIATO,
    QUEST_COMPLETATA,
    MESSAGGIO_MOSTRATO,
    ATTO_COMPLETATO,
    MINIGIOCO_AVVIATO,
    MINIGIOCO_FASE_CAMBIATA,
    MINIGIOCO_INDICATORE_AGGIORNATO, // payload: Integer posizione 0-100
    MINIGIOCO_ERBA_ESITO,            // payload: EsitoErba (idErba, corretta)
    MINIGIOCO_COLPO_ESITO,           // payload: EsitoColpo (successo, posizione)
    MINIGIOCO_COMPLETATO,            // payload: String idPuzzle
}
