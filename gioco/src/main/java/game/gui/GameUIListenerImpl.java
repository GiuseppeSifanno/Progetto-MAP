package game.gui;

import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.minigioco.MontacarichiManager;
import game.minigioco.ZuppaFogliantiManager;
import game.model.PassoQuestCompletato;
import game.model.SceltaEffettuata;

import javax.swing.SwingUtilities;

public class GameUIListenerImpl implements GameUIListener {

    /** Id payload delle interazioni sintetiche usate dai minigiochi per notificare il completamento. */
    private static final String ID_ZUPPA_COMPLETATA = "int_giungla_zuppa_completata";
    private static final String ID_MONTACARICHI_COMPLETATO = "int_miniera_montacarichi";

    private final GestoreSchermate gestoreSchermate;

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

        if (oggetto != null && "o5".equals(oggetto.getId())) {
            gestoreSchermate.getGamePanel().mostraPergamena();
        }
        if (oggetto != null && "o13".equals(oggetto.getId())) {
            gestoreSchermate.getGamePanel().nascondiBannerAvviso();
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
    public void onMessaggioMostrato(String messaggio) {
        gestoreSchermate.getGamePanel().mostraMessaggio(messaggio);
    }

    @Override
    public void onQuestCompletata(PassoQuestCompletato passo) {
        gestoreSchermate.getQuestPanel().aggiorna();
    }

    // ==================== Zuppa Foglianti ====================

    @Override
    public void onMinigiocoAvviato() {
        gestoreSchermate.getGamePanel().avviaFaseRaccoltaErbe();
    }

    @Override
    public void onMinigiocoErbaEsito(ZuppaFogliantiManager.EsitoErba esito) {
        gestoreSchermate.getGamePanel().mostraEsitoErba(esito);
    }

    // ==================== Montacarichi ====================

    @Override
    public void onMinigiocoFaseCambiataMontacarichi(MontacarichiManager.Fase fase) {
        if (fase == MontacarichiManager.Fase.COMBATTENTE) {
            gestoreSchermate.getGamePanel().mostraFaseCombattenteMontacarichi();
        } else {
            gestoreSchermate.getGamePanel().mostraFaseNavigatriceMontacarichi();
        }
    }

    @Override
    public void onMinigiocoIndicatoreAggiornato(int posizione) {
        // Il tick arriva da un thread separato del manager: l'update Swing
        // deve sempre passare dall'EDT.
        SwingUtilities.invokeLater(() ->
                gestoreSchermate.getGamePanel().aggiornaIndicatoreMontacarichi(posizione)
        );
    }

    @Override
    public void onMinigiocoColpoEsito(MontacarichiManager.EsitoColpo esito) {
        gestoreSchermate.getGamePanel().mostraEsitoColpoMontacarichi(esito);
    }

    @Override
    public void onMinigiocoNodoEsito(MontacarichiManager.EsitoNodo esito) {
        gestoreSchermate.getGamePanel().mostraEsitoNodoMontacarichi(esito);
    }

    // ==================== Comune ====================

    @Override
    public void onMinigiocoCompletato(String idPayload) {
        if (ID_MONTACARICHI_COMPLETATO.equals(idPayload)) {
            gestoreSchermate.getInventarioPanel().getBtnChiudi().doClick();
            gestoreSchermate.getGamePanel().completaMontacarichiUI();
        } else if (ID_ZUPPA_COMPLETATA.equals(idPayload)) {
            gestoreSchermate.getInventarioPanel().getBtnChiudi().doClick();
            gestoreSchermate.getGamePanel().mostraZuppaCompletata();
        }
    }
}