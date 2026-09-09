package game.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pannello dedicato al mini gioco del Montacarichi.
 * Gestisce le due fasi:
 * 1. Combattente: colpire quando l'indicatore è nella zona verde;
 * 2. Navigatrice: cliccare i tre nodi nell'ordine corretto.
 */
public class MontacarichiPanel extends JPanel {

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

    // Dimensioni e layout
    private static final int DIMENSIONE_NODO = 90;
    private static final int[][] NODI = {
            {560, 260},
            {680, 620},
            {760, 980}
    };

    // Stili e Colori
    private static final Color COLORE_TESTO_PRIMARIO = new Color(240, 220, 190);
    private static final Color COLORE_BORDO_DORATO = new Color(198, 156, 109);
    private static final Color COLORE_SFONDO_SCURO = new Color(20, 15, 10, 235);
    private static final Color COLORE_SFONDO_BANNER = new Color(15, 15, 20, 210);
    private static final Color COLORE_SFONDO_BARRA = new Color(60, 45, 35);
    private static final Color COLORE_ZONA_VERDE = new Color(80, 200, 100, 190);

    private static final Color NODO_BASE_ATTIVO = new Color(60, 140, 220);
    private static final Color NODO_BASE_COMPLETATO = new Color(212, 175, 55);
    private static final Color NODO_ALONE_ATTIVO = new Color(120, 200, 255, 130);
    private static final Color NODO_ALONE_COMPLETATO = new Color(255, 220, 120, 130);

    private final PannelloSfondo sfondo;
    private final GestoreComponenti gestore;
    private final Runnable onAvvio;
    private final Runnable onCompletato;
    private final Consumer<String> mostraMessaggio;

    private Timer timerIndicatore;
    private int posizioneIndicatore = 0;
    private int direzioneIndicatore = 1;
    private int colpiRiusciti = 0;

    private JPanel pannelloBarraTensione;
    private JButton btnColpisci;
    private JLabel bannerFase;

    private int prossimoNodoAtteso = 0;
    private Timer timerPulseNodi;
    private final List<JButton> nodiAttivi = new ArrayList<>();

    private JPanel overlayAvvia;

    public MontacarichiPanel(
            PannelloSfondo sfondo,
            GestoreComponenti gestore,
            Runnable onAvvio,
            Runnable onCompletato,
            Consumer<String> mostraMessaggio
    ) {
        this.sfondo = sfondo;
        this.gestore = gestore;
        this.onAvvio = onAvvio;
        this.onCompletato = onCompletato;
        this.mostraMessaggio = mostraMessaggio;

        setOpaque(false);
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        pannelloBarraTensione = creaBarraTensione();
        sfondo.add(pannelloBarraTensione);
        gestore.registra(pannelloBarraTensione, 1050, 550, 110, 420);
        pannelloBarraTensione.setVisible(false);

        btnColpisci = creaBottoneColpisci();
        sfondo.add(btnColpisci);
        gestore.registra(btnColpisci, 1050, 830, 200, 80);
        btnColpisci.setVisible(false);

        bannerFase = creaBannerFase();
        sfondo.add(bannerFase);
        gestore.registraCentratoInBasso(bannerFase, 1100, 90, 20);
        bannerFase.setVisible(false);

        overlayAvvia = creaOverlayAvvia();
        sfondo.add(overlayAvvia);
        gestore.registraCentrato(overlayAvvia, 520, 220);
        overlayAvvia.setVisible(false);
    }

    private JButton creaBottoneColpisci() {
        JButton bottone = new JButton("COLPISCI");
        bottone.setFont(GamePanel.caricaFontAntico(20f));
        bottone.setForeground(COLORE_TESTO_PRIMARIO);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setBackground(COLORE_SFONDO_SCURO);
        bottone.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(16, COLORE_BORDO_DORATO),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        bottone.setFocusPainted(false);
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottone.addActionListener(e -> onColpisci());
        return bottone;
    }

    private JLabel creaBannerFase() {
        JLabel banner = new JLabel("", SwingConstants.CENTER);
        ZuppaFogliantiPanel.setStyle(banner, COLORE_SFONDO_BANNER);
        return banner;
    }

    private JPanel creaOverlayAvvia() {
        JPanel pannello = creaOverlayBase();

        JButton btnAvvia = new JButton("AVVIA MINIGIOCO");
        ZuppaFogliantiPanel.setStyle(btnAvvia, COLORE_TESTO_PRIMARIO);
        btnAvvia.addActionListener(e -> {
            overlayAvvia.setVisible(false);
            avviaMinigioco();
        });

        pannello.add(btnAvvia, BorderLayout.CENTER);
        return pannello;
    }

    private JPanel creaOverlayBase() {
        JPanel pannello = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO_SCURO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, COLORE_BORDO_DORATO),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        return pannello;
    }

    public void mostraBottoneAvviaMontacarichi() {
        sfondo.setComponentZOrder(overlayAvvia, 0);
        overlayAvvia.setVisible(true);
        overlayAvvia.revalidate();
        overlayAvvia.repaint();
    }

    public void avviaMinigioco() {
        onAvvio.run();
        sfondo.setImmagineSfondo("/assets/Montacarichi.png");

        colpiRiusciti = 0;
        pannelloBarraTensione.setVisible(true);
        btnColpisci.setVisible(true);

        bannerFase.setText(
                "<html><div style='text-align:center;'>Il Combattente tiene teso il cavo: premi <b>COLPISCI</b> "
                        + "quando l'indicatore è nella zona verde!</div></html>"
        );
        bannerFase.setVisible(true);

        avviaIndicatore();
    }

    private JPanel creaBarraTensione() {
        JPanel pannello = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Sfondo barra
                g2.setColor(COLORE_SFONDO_BARRA);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Zona verde obiettivo
                int yZonaTop = h - (int) (h * (ZONA_VERDE_MAX / 100.0));
                int yZonaBottom = h - (int) (h * (ZONA_VERDE_MIN / 100.0));
                g2.setColor(COLORE_ZONA_VERDE);
                g2.fillRect(2, yZonaTop, w - 4, yZonaBottom - yZonaTop);

                // Linea indicatore mobile
                int yIndicatore = h - (int) (h * (posizioneIndicatore / 100.0));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, Math.max(0, Math.min(h - 6, yIndicatore - 3)), w, 6);

                // Bordo
                g2.setColor(COLORE_BORDO_DORATO);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 16, 16);

                g2.dispose();
            }
        };

        pannello.setOpaque(false);
        return pannello;
    }

    private void avviaIndicatore() {
        posizioneIndicatore = 0;
        direzioneIndicatore = 1;
        arrestaTimer(timerIndicatore);

        timerIndicatore = new Timer(TIMER_INDICATORE_DELAY_MS, e -> {
            aggiornaPosizioneIndicatore();
            pannelloBarraTensione.repaint();
        });

        timerIndicatore.start();
    }

    private void aggiornaPosizioneIndicatore() {
        posizioneIndicatore += direzioneIndicatore * PASSO_INDICATORE;

        if (posizioneIndicatore >= INDICATORE_MAX) {
            posizioneIndicatore = INDICATORE_MAX;
            direzioneIndicatore = -1;
        } else if (posizioneIndicatore <= 0) {
            posizioneIndicatore = 0;
            direzioneIndicatore = 1;
        }
    }

    private void fermaIndicatore() {
        arrestaTimer(timerIndicatore);
    }

    private void onColpisci() {
        boolean successo = posizioneIndicatore >= ZONA_VERDE_MIN
                && posizioneIndicatore <= ZONA_VERDE_MAX;

        if (successo) {
            colpiRiusciti++;
            mostraMessaggio.accept("Colpo riuscito! (" + colpiRiusciti + "/" + COLPI_RICHIESTI + ")");

            if (colpiRiusciti >= COLPI_RICHIESTI) {
                fermaIndicatore();
                avviaFaseNavigatrice();
            }
        } else {
            mostraMessaggio.accept("Troppo presto o troppo tardi, riprova!");
        }
    }

    private void avviaFaseNavigatrice() {
        pannelloBarraTensione.setVisible(false);
        btnColpisci.setVisible(false);

        bannerFase.setText(
                "<html><div style='text-align:center;'>La Navigatrice calcola l'intreccio: clicca i 3 nodi "
                        + "nell'ordine numerico giusto (1 → 2 → 3).</div></html>"
        );

        prossimoNodoAtteso = 0;
        creaNodi();
    }

    private void creaNodi() {
        rimuoviNodi();

        for (int i = 0; i < NODI.length; i++) {
            int indice = i;
            JButton nodo = creaNodo(i + 1);

            nodo.addActionListener(e -> onNodoCliccato(indice, nodo));
            gestore.registra(nodo, NODI[i][0], NODI[i][1], DIMENSIONE_NODO, DIMENSIONE_NODO);

            nodiAttivi.add(nodo);
        }

        arrestaTimer(timerPulseNodi);

        timerPulseNodi = new Timer(TIMER_PULSE_DELAY_MS, e -> {
            for (JButton nodo : nodiAttivi) {
                if (nodo.isEnabled()) {
                    nodo.repaint();
                }
            }
        });

        timerPulseNodi.start();
    }

    private JButton creaNodo(int numero) {
        JButton nodo = new JButton(String.valueOf(numero)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int d = Math.min(w, h) - 10;
                int x = (w - d) / 2;
                int y = (h - d) / 2;

                boolean completato = !isEnabled();
                Color base = completato ? NODO_BASE_COMPLETATO : NODO_BASE_ATTIVO;
                Color coloreAlone = completato ? NODO_ALONE_COMPLETATO : NODO_ALONE_ATTIVO;

                int haloExtra = completato
                        ? 4
                        : (int) (6 + 6 * Math.sin(System.currentTimeMillis() / PULSE_FREQUENCY_DIVISOR));

                // Disegno alone pulsante
                g2.setColor(coloreAlone);
                g2.fillOval(x - haloExtra, y - haloExtra, d + haloExtra * 2, d + haloExtra * 2);

                // Disegno corpo nodo con gradiente
                g2.setPaint(new GradientPaint(x, y, base.brighter(), x, y + d, base.darker()));
                g2.fillOval(x, y, d, d);

                // Bordo
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x, y, d, d);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        nodo.setContentAreaFilled(false);
        nodo.setBorderPainted(false);
        nodo.setOpaque(false);
        nodo.setFocusPainted(false);
        nodo.setForeground(Color.WHITE);
        nodo.setFont(nodo.getFont().deriveFont(Font.BOLD, 26f));
        nodo.setHorizontalAlignment(SwingConstants.CENTER);
        nodo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return nodo;
    }

    private void onNodoCliccato(int indice, JButton nodo) {
        if (indice == prossimoNodoAtteso) {
            nodo.setEnabled(false);
            nodo.repaint();
            prossimoNodoAtteso++;

            if (prossimoNodoAtteso >= NODI.length) {
                completaMinigioco();
            }
        } else {
            mostraMessaggio.accept("Non è questo il nodo giusto!");
        }
    }

    private void rimuoviNodi() {
        arrestaTimer(timerPulseNodi);

        for (JButton nodo : nodiAttivi) {
            gestore.rimuovi(nodo);
        }

        nodiAttivi.clear();
    }

    private void arrestaTimer(Timer timer) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private void completaMinigioco() {
        rimuoviNodi();
        fermaIndicatore();

        bannerFase.setVisible(false);
        onCompletato.run();
    }

    public void reset() {
        fermaIndicatore();
        rimuoviNodi();

        if (pannelloBarraTensione != null) {
            pannelloBarraTensione.setVisible(false);
        }
        if (btnColpisci != null) {
            btnColpisci.setVisible(false);
        }
        if (bannerFase != null) {
            bannerFase.setVisible(false);
        }
        if (overlayAvvia != null) {
            overlayAvvia.setVisible(false);
        }

        colpiRiusciti = 0;
        prossimoNodoAtteso = 0;
        posizioneIndicatore = 0;
        direzioneIndicatore = 1;
    }
}