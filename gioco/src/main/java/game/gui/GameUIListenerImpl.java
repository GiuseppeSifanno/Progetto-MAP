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
        gestoreSchermate.getGamePanel().aggiornaDialogo(dialogo);
    }

    @Override
    public void onSceltaEffettuata(SceltaEffettuata scelta) {

    }

    @Override
    public void onOggettoAggiunto(BaseOggetto oggetto) {
        gestoreSchermate.getInventarioPanel().aggiorna();

        // La borsa recuperata dal fiume (Atto 2) contiene la pergamena: la mostriamo a schermo.
        if (oggetto != null && "o5".equals(oggetto.getId())) {
            gestoreSchermate.getGamePanel().mostraPergamena();
        }
    }

    @Override
    public void onOggettoRimosso(BaseOggetto oggetto) {
        gestoreSchermate.getInventarioPanel().aggiorna();
    }

    @Override
    public void onAttoCambiato(String idAtto) {
        gestoreSchermate.getGamePanel().aggiorna();
    }

    @Override
    public void onPuzzleRisolto(String idPuzzle) {

    }

    @Override
    public void onMessaggioMostrato(String messaggio) {
        gestoreSchermate.getGamePanel().mostraMessaggio(messaggio);
    }

    @Override
    public void onQuestCompletata(PassoQuestCompletato passo) {
        gestoreSchermate.getQuestPanel().aggiorna();
    }
}