package game.ui;

import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.model.PassoQuestCompletato;
import game.model.SceltaEffettuata;

public interface GameUIListener {
    void onDialogoCambiato(BaseDialogo dialogo);
    void onSceltaEffettuata(SceltaEffettuata scelta);
    void onOggettoAggiunto(BaseOggetto oggetto);
    void onOggettoRimosso(BaseOggetto oggetto);
    void onAttoCambiato(String idAtto);
    void onPuzzleRisolto(String idPuzzle);
    void onMessaggioMostrato(String messaggio);
    void onQuestCompletata(PassoQuestCompletato passo);
}