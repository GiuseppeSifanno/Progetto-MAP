package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pannello dedicato al minigioco delle Liane (Atto 4).
 * Meccanica "tendi e rilascia": un nodo alla volta è attivo; tenendo premuto
 * un anello si riempie da solo. Il giocatore deve RILASCIARE il tasto mentre
 * l'anello è dentro la fascia verde disegnata sul cerchio: troppo presto o
 * troppo tardi (o se non rilasci affatto e l'anello si riempie del tutto) il
 * tentativo fallisce e riparte da zero sullo stesso nodo. Completati 3 nodi
 * le liane crollano.
 */
public class LianePanel extends JPanel {

    private static final int NODI_RICHIESTI = 3;

    // Riempimento dell'anello: da 0 a 100 in circa 1.5s di pressione continua
    private static final int TIMER_CARICA_DELAY_MS = 30;
    private static final int INCREMENTO_CARICA = 2;

    // Fascia verde sull'anello: larghezza fissa, ma posizione randomizzata
    // ogni volta che un nodo diventa attivo.
    private static final int LARGHEZZA_ZONA_VERDE = 20;
    private int zonaVerdeMin = 0;
    private int zonaVerdeMax = LARGHEZZA_ZONA_VERDE;

    // Dimensioni e layout (coordinate PROVVISORIE sull'immagine originale,
    // da calibrare quando arriva l'immagine reale)
    private static final int DIMENSIONE_NODO = 110;
    private static final int[][] NODI = {
            {400, 300},
            {768, 250},
            {1130, 300}
    };

    // Stili e colori (stessa palette del resto del gioco)
    private static final Color COLORE_TESTO_PRIMARIO = new Color(240, 220, 190);
    private static final Color COLORE_BORDO_DORATO = new Color(198, 156, 109);
    private static final Color COLORE_SFONDO_SCURO = new Color(20, 15, 10, 235);
    private static final Color COLORE_SFONDO_BANNER = new Color(15, 15, 20, 210);
    private static final Color COLORE_ZONA_VERDE = new Color(80, 200, 100, 220);

    private static final Color NODO_BASE_ATTIVO = new Color(60, 140, 220);
    private static final Color NODO_BASE_COMPLETATO = new Color(212, 175, 55);
    private static final Color NODO_BASE_INATTIVO = new Color(90, 90, 90);

    private final PannelloSfondo sfondo;
    private final GestoreComponenti gestore;
    private final Runnable onAvvio;
    private final Runnable onCompletato;
    private final Consumer<String> mostraMessaggio;

    private JLabel bannerFase;
    private JPanel overlayAvvia;

    private int nodoAttivo = 0;
    private int caricaNodoAttivo = 0;
    private Timer timerCarica;
    private final List<JButton> nodi = new ArrayList<>();

    public LianePanel(
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
        bannerFase = creaBannerFase();
        sfondo.add(bannerFase);
        gestore.registraCentratoInBasso(bannerFase, 1100, 90, 20);
        bannerFase.setVisible(false);

        overlayAvvia = creaOverlayAvvia();
        sfondo.add(overlayAvvia);
        gestore.registraCentrato(overlayAvvia, 520, 220);
        overlayAvvia.setVisible(false);
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

    public void mostraBottoneAvvia() {
        sfondo.setComponentZOrder(overlayAvvia, 0);
        overlayAvvia.setVisible(true);
        overlayAvvia.revalidate();
        overlayAvvia.repaint();
    }

    public void avviaMinigioco() {
        onAvvio.run();

        nodoAttivo = 0;
        caricaNodoAttivo = 0;

        bannerFase.setText(
                "<html><div style='text-align:center;'>Tieni premuto su un nodo: l'anello si riempie da solo. "
                        + "<b>Rilascia</b> quando entra nella fascia verde per strapparlo!</div></html>"
        );
        bannerFase.setVisible(true);

        creaNodi();
    }

    private void creaNodi() {
        rimuoviNodi();
        randomizzaZonaVerde();

        for (int i = 0; i < NODI.length; i++) {
            JButton nodo = creaNodo(i);
            nodo.setEnabled(i == nodoAttivo);

            MouseAdapter gestisciPressione = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!nodo.isEnabled()) return;
                    iniziaCarica();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!nodo.isEnabled()) return;
                    valutaRilascio();
                }
            };
            nodo.addMouseListener(gestisciPressione);

            gestore.registra(nodo, NODI[i][0], NODI[i][1], DIMENSIONE_NODO, DIMENSIONE_NODO);
            nodi.add(nodo);
        }
    }

    /** Nodo circolare: grigio se in attesa, blu con anello di carica + fascia verde se attivo, oro se completato. */
    private JButton creaNodo(int indice) {
        JButton nodo = new JButton(String.valueOf(indice + 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int d = Math.min(w, h) - 16;
                int x = (w - d) / 2;
                int y = (h - d) / 2;

                boolean completato = Boolean.TRUE.equals(getClientProperty("completato"));
                boolean attivo = indice == nodoAttivo && !completato;

                Color base = completato ? NODO_BASE_COMPLETATO : (attivo ? NODO_BASE_ATTIVO : NODO_BASE_INATTIVO);

                g2.setPaint(new GradientPaint(x, y, base.brighter(), x, y + d, base.darker()));
                g2.fillOval(x, y, d, d);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x, y, d, d);

                if (attivo) {
                    // Fascia verde fissa sull'anello (traguardo di rilascio)
                    double angoloZonaMin = 360.0 * zonaVerdeMin / 100.0;
                    double angoloZonaMax = 360.0 * zonaVerdeMax / 100.0;
                    g2.setColor(COLORE_ZONA_VERDE);
                    g2.setStroke(new BasicStroke(9, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                    g2.drawArc(x - 9, y - 9, d + 18, d + 18, (int) (90 - angoloZonaMax),
                            (int) (angoloZonaMax - angoloZonaMin));

                    // Anello di carica che avanza mentre tieni premuto
                    double angoloCarica = 360.0 * caricaNodoAttivo / 100.0;
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(x - 8, y - 8, d + 16, d + 16, 90, -(int) Math.round(angoloCarica));
                }

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

    private void iniziaCarica() {
        caricaNodoAttivo = 0;
        arrestaTimer(timerCarica);

        timerCarica = new Timer(TIMER_CARICA_DELAY_MS, e -> {
            caricaNodoAttivo = Math.min(100, caricaNodoAttivo + INCREMENTO_CARICA);
            nodi.get(nodoAttivo).repaint();

            if (caricaNodoAttivo >= 100) {
                // Non rilasciato in tempo: fallito, l'anello si è riempito del tutto
                arrestaTimer(timerCarica);
                fallisciTentativo();
            }
        });
        timerCarica.start();
    }

    private void valutaRilascio() {
        arrestaTimer(timerCarica);

        boolean successo = caricaNodoAttivo >= zonaVerdeMin && caricaNodoAttivo <= zonaVerdeMax;
        if (successo) {
            completaNodoAttivo();
        } else {
            fallisciTentativo();
        }
    }

    private void fallisciTentativo() {
        caricaNodoAttivo = 0;
        randomizzaZonaVerde();
        mostraMessaggio.accept("Troppo presto o troppo tardi, riprova!");
        if (nodoAttivo < nodi.size()) {
            nodi.get(nodoAttivo).repaint();
        }
    }

    private void completaNodoAttivo() {
        JButton nodo = nodi.get(nodoAttivo);
        nodo.putClientProperty("completato", Boolean.TRUE);
        nodo.setEnabled(false);
        nodo.repaint();

        nodoAttivo++;
        caricaNodoAttivo = 0;

        if (nodoAttivo >= NODI.length) {
            completaMinigioco();
        } else {
            mostraMessaggio.accept("Nodo strappato! (" + nodoAttivo + "/" + NODI.length + ")");
            randomizzaZonaVerde();
            nodi.get(nodoAttivo).setEnabled(true);
        }
    }

    /** Sceglie una nuova posizione casuale per la fascia verde (larghezza fissa). */
    private void randomizzaZonaVerde() {
        int margineMassimo = 100 - LARGHEZZA_ZONA_VERDE;
        zonaVerdeMin = (int) (Math.random() * margineMassimo);
        zonaVerdeMax = zonaVerdeMin + LARGHEZZA_ZONA_VERDE;
    }

    private void rimuoviNodi() {
        for (JButton nodo : nodi) {
            gestore.rimuovi(nodo);
        }
        nodi.clear();
    }

    private void arrestaTimer(Timer timer) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private void completaMinigioco() {
        arrestaTimer(timerCarica);
        bannerFase.setVisible(false);
        rimuoviNodi();
        onCompletato.run();
    }

    public void reset() {
        arrestaTimer(timerCarica);
        rimuoviNodi();

        if (bannerFase != null) {
            bannerFase.setVisible(false);
        }
        if (overlayAvvia != null) {
            overlayAvvia.setVisible(false);
        }

        nodoAttivo = 0;
        caricaNodoAttivo = 0;
    }
}