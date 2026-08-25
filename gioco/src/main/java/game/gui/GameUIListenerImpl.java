package game.gui;

import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.model.PassoQuestCompletato;
import game.model.SceltaEffettuata;

public class GameUIListenerImpl implements GameUIListener{

    private GestoreSchermate gestoreSchermate;

    public GameUIListenerImpl(GestoreSchermate gestoreSchermate) {
        this.gestoreSchermate = gestoreSchermate;
    }

    @Override
    public void onDialogoCambiato(BaseDialogo dialogo) {

    }

    @Override
    public void onSceltaEffettuata(SceltaEffettuata scelta) {

    }

    @Override
    public void onOggettoAggiunto(BaseOggetto oggetto) {
        gestoreSchermate.getInventarioPanel().aggiorna();
    }

    @Override
    public void onOggettoRimosso(BaseOggetto oggetto) {
        gestoreSchermate.getInventarioPanel().aggiorna();
    }

    @Override
    public void onAttoCambiato(String idAtto) {

    }

    @Override
    public void onPuzzleRisolto(String idPuzzle) {

    }

    @Override
    public void onMessaggioMostrato(String messaggio) {

    }

    @Override
    public void onQuestCompletata(PassoQuestCompletato passo) {

    }
}
