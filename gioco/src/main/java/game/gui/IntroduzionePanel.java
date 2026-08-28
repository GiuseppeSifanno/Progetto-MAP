package game.gui;

import engine.model.Battuta;
import game.manager.GameManager;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IntroduzionePanel extends BasePanel {

    /** Le tre fasi in sequenza della cinematica introduttiva. */
    private enum Fase {
        TESTO_INIZIALE,
        FADE_IMMAGINE,
        DIALOGO
    }

    private static final int INTERVALLO_MS = 30;

    // ===== Fase 1: testo iniziale =====
    private static final String TESTO_INIZIALE = "DA QUALCHE PARTE DEL MONDO";
    private static final int TICK_PER_CARATTERE = 2;   // 1 carattere ogni 2 tick -> ~60ms/carattere
    private static final int TICK_PAUSA_LETTURA = 60;  // ~1.8s di pausa a testo completo

    // ===== Fase 2: fade immagine =====
    private static final float TEMPO_FADE_SEC = 2f;

    // ===== Fase 3: dialogo =====
    private static final int TICK_PAUSA_DIALOGO_FINALE = 90; // pausa dopo l'ultima battuta

    private final GestoreSchermate gestoreSchermate;
    private final BufferedImage sfondo;
    private final Clip audio;

    private Fase faseCorrente;
    private final Timer timer;

    // stato Fase 1
    private int tickCarattereTesto;   // tick trascorsi dall'ultimo carattere aggiunto
    private int indiceCarattereTesto; // quanti caratteri del testo iniziale sono già "scritti"
    private int tickAttesa;           // contatore generico di pausa, riusato tra le fasi

    // stato Fase 2
    private float alpha = 1f;
    private final float incrementoAlpha;

    // stato Fase 3
    private List<Battuta> battute = List.of();
    private int indiceBattuta;
    private int indiceCarattereBattuta;
    private int tickCarattereBattuta;
    private int tickAttesaFinale;

    public IntroduzionePanel(GestoreSchermate gestoreSchermate, GameManager gameManager) {
        super(gameManager);
        this.gestoreSchermate = gestoreSchermate;
        this.sfondo = caricaImmagine("/assets/frutti.png");
        this.audio = caricaAudio("/assets/musica/mare-agitato.wav");

        int numeroTickFade = (int) ((TEMPO_FADE_SEC * 1000) / INTERVALLO_MS);
        this.incrementoAlpha = 1f / numeroTickFade;

        // Un solo timer, sempre allo stesso intervallo, per tutte e tre le fasi.
        this.timer = new Timer(INTERVALLO_MS, e -> tick());
    }

    @Override
    public void init() {
        audio.setFramePosition(0);
        audio.loop(Clip.LOOP_CONTINUOUSLY);

        // reset completo dello stato: si riparte sempre dalla Fase 1
        faseCorrente = Fase.TESTO_INIZIALE;
        tickCarattereTesto = 0;
        indiceCarattereTesto = 0;
        tickAttesa = 0;
        tickAttesaFinale = 0;
        alpha = 1f;

        battute = gameManager.getDialogManager().getDialogo() != null
                ? gameManager.getDialogManager().getDialogo().getBattute()
                : List.of();
        indiceBattuta = 0;
        indiceCarattereBattuta = 0;
        tickCarattereBattuta = 0;

        timer.start();
    }

    public void stop() {
        audio.stop();
        audio.setFramePosition(0);
        timer.stop();
    }

    /** Chiamato ad ogni tick del timer: agisce in base alla fase corrente. */
    private void tick() {
        switch (faseCorrente) {
            case TESTO_INIZIALE -> aggiornaTestoIniziale();
            case FADE_IMMAGINE -> aggiornaFade();
            case DIALOGO -> aggiornaDialogo();
        }
        repaint();
    }

    // ==================== FASE 1 ====================

    private void aggiornaTestoIniziale() {
        if (indiceCarattereTesto < TESTO_INIZIALE.length()) {
            tickCarattereTesto++;
            if (tickCarattereTesto >= TICK_PER_CARATTERE) {
                tickCarattereTesto = 0;
                indiceCarattereTesto++;
            }
            return;
        }

        // testo completo: attesa di lettura prima di passare alla fase successiva
        tickAttesa++;
        if (tickAttesa >= TICK_PAUSA_LETTURA) {
            tickAttesa = 0;
            faseCorrente = Fase.FADE_IMMAGINE; // trigger: fine fase 1 -> inizio fase 2
        }
    }

    // ==================== FASE 2 ====================

    private void aggiornaFade() {
        if (alpha - incrementoAlpha <= 0) {
            alpha = 0;
            faseCorrente = Fase.DIALOGO; // trigger: fine fase 2 -> inizio fase 3
        } else {
            alpha -= incrementoAlpha;
        }
    }

    // ==================== FASE 3 ====================

    private void aggiornaDialogo() {
        if (battute.isEmpty()) {
            terminaIntroduzione();
            return;
        }

        String testoBattuta = battute.get(indiceBattuta).testo();
        if (testoBattuta == null) testoBattuta = "";

        if (indiceCarattereBattuta < testoBattuta.length()) {
            tickCarattereBattuta++;
            if (tickCarattereBattuta >= TICK_PER_CARATTERE) {
                tickCarattereBattuta = 0;
                indiceCarattereBattuta++;
            }
            return;
        }

        // battuta corrente completa: avanti alla prossima dopo una pausa
        if (indiceBattuta < battute.size() - 1) {
            tickAttesa++;
            if (tickAttesa < TICK_PAUSA_LETTURA) return;
            tickAttesa = 0;
            indiceBattuta++;
            indiceCarattereBattuta = 0;
        } else {
            tickAttesaFinale++;
            if (tickAttesaFinale >= TICK_PAUSA_DIALOGO_FINALE) {
                terminaIntroduzione();
            }
        }
    }

    public void skipDialogo() {
        String testoBattuta = battute.get(indiceBattuta).testo();
        indiceCarattereBattuta = testoBattuta.length();
        tickAttesa = (int) (TICK_PAUSA_LETTURA / 1.5) ;
    }

    private void terminaIntroduzione() {
        stop();
        if (gameManager.prossimoAtto())
            gestoreSchermate.mostra(GestoreSchermate.GAME);

    }

    // ==================== DISEGNO ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (sfondo != null && faseCorrente != Fase.TESTO_INIZIALE) {
            disegnaSfondoScalato(g2);
        }

        if (faseCorrente == Fase.FADE_IMMAGINE || faseCorrente == Fase.TESTO_INIZIALE) {
            Composite originale = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(originale);
        }

        switch (faseCorrente) {
            case TESTO_INIZIALE -> disegnaTestoIniziale(g2);
            case DIALOGO -> disegnaBattutaCorrente(g2);
            default -> { }
        }

        g2.dispose();
    }

    private void disegnaTestoIniziale(Graphics2D g2) {
        String visibile = TESTO_INIZIALE.substring(0, indiceCarattereTesto);

        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(visibile)) / 2;
        int y = getHeight() / 2;
        g2.drawString(visibile, x, y);
    }

    private void disegnaBattutaCorrente(Graphics2D g2) {
        if (battute.isEmpty()) return;

        String testoBattuta = battute.get(indiceBattuta).testo();
        if (testoBattuta == null) testoBattuta = "";
        String visibile = testoBattuta.substring(0, Math.min(indiceCarattereBattuta, testoBattuta.length()));

        g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);

        // disegno semplice in basso, senza wrapping: da rifinire quando decidi lo stile
        g2.drawString(visibile, 60, getHeight() - 80);
    }

    private void disegnaSfondoScalato(Graphics2D g2) {
        int panelW = getWidth();
        int panelH = getHeight();
        int imgW = sfondo.getWidth();
        int imgH = sfondo.getHeight();

        double scala = Math.min((double) panelW / imgW, (double) panelH / imgH);
        int nuovaW = (int) (imgW * scala);
        int nuovaH = (int) (imgH * scala);
        int x = (panelW - nuovaW) / 2;
        int y = (panelH - nuovaH) / 2;

        g2.drawImage(sfondo, x, y, nuovaW, nuovaH, this);
    }

    private BufferedImage caricaImmagine(String percorso) {
        try (InputStream stream = getClass().getResourceAsStream(percorso)) {
            if (stream == null) {
                throw new IllegalArgumentException("Risorsa non trovata: " + percorso);
            }
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento dell'immagine: " + percorso, e);
        }
    }

    private Clip caricaAudio(String percorso) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(percorso));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
            throw new RuntimeException("Impossibile caricare l'audio: " + percorso, e);
        }
    }

    @Override
    public void aggiorna() { }

    @Override
    public void reset() {
        stop();
    }
}