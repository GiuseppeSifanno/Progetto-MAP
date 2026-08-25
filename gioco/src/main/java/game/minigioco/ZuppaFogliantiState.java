package game.minigioco;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ZuppaFogliantiState {

    public enum Fase { IDLE, NAVIGATRICE, COMBATTENTE, CAPITANO, COMPLETATO }

    private final AtomicReference<Fase> faseCorrente = new AtomicReference<>(Fase.IDLE);
    private final AtomicInteger erbeCorretteRaccolte = new AtomicInteger(0);
    private final AtomicInteger colpiRiusciti = new AtomicInteger(0);
    private final AtomicInteger posizioneIndicatore = new AtomicInteger(0);
    private final AtomicBoolean threadAttivo = new AtomicBoolean(false);

    // ---------- Fase ----------

    public Fase getFaseCorrente() {
        return faseCorrente.get();
    }

    public void setFaseCorrente(Fase fase) {
        faseCorrente.set(fase);
    }

    // ---------- Erbe (fase Navigatrice) ----------

    /** Incrementa il contatore delle erbe corrette raccolte e restituisce il nuovo totale. */
    public int incrementaErbeCorrette() {
        return erbeCorretteRaccolte.incrementAndGet();
    }

    public int getErbeCorretteRaccolte() {
        return erbeCorretteRaccolte.get();
    }

    // ---------- Colpi (fase Combattente) ----------

    /** Incrementa il contatore dei colpi riusciti e restituisce il nuovo totale. */
    public int incrementaColpiRiusciti() {
        return colpiRiusciti.incrementAndGet();
    }

    public int getColpiRiusciti() {
        return colpiRiusciti.get();
    }

    // ---------- Indicatore (thread fase Combattente) ----------

    public int getPosizioneIndicatore() {
        return posizioneIndicatore.get();
    }

    public void setPosizioneIndicatore(int posizione) {
        posizioneIndicatore.set(posizione);
    }

    // ---------- Thread ----------

    public boolean isThreadAttivo() {
        return threadAttivo.get();
    }

    public void setThreadAttivo(boolean attivo) {
        threadAttivo.set(attivo);
    }

    // ---------- Reset ----------

    /**
     * Riporta lo stato del minigioco a IDLE e azzera tutti i contatori.
     * Non ferma il thread da sola: chi chiama reset() (il Manager) deve
     * aver già fermato l'executor prima di invocarla, altrimenti il thread
     * in esecuzione potrebbe continuare a scrivere su posizioneIndicatore
     * dopo il reset.
     */
    public void reset() {
        faseCorrente.set(Fase.IDLE);
        erbeCorretteRaccolte.set(0);
        colpiRiusciti.set(0);
        posizioneIndicatore.set(0);
        threadAttivo.set(false);
    }
}