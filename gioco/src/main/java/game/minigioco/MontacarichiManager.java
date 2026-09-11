package game.minigioco;

import javax.swing.*;

public class MontacarichiManager {
    // Parametri di gioco
    private static final int COLPI_RICHIESTI = 3;
    private static final int ZONA_VERDE_MIN = 40;
    private static final int ZONA_VERDE_MAX = 60;
    private static final int INDICATORE_MAX = 100;
    private static final int PASSO_INDICATORE = 3;


    // Timer delays (ms)
    private static final int TIMER_INDICATORE_DELAY_MS = 30;
    private static final int TIMER_PULSE_DELAY_MS = 40;
    private static final double PULSE_FREQUENCY_DIVISOR = 260.0;

    private Timer timerIndicatore;
    private int posizioneIndicatore = 0;
    private int direzioneIndicatore = 1;
    private Runnable onAvvio;
    private Runnable onCompletato;

    private int prossimoNodoAtteso = 0;
    private Timer timerPulseNodi;
    private int colpiRiusciti = 0;

    public void MontacarichiManager() {

    }


    public void avviaMinigioco() {
        onAvvio.run();
    }
}
