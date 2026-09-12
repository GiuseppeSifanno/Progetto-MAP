package game.gui;

import engine.GUI.BasePanel;
import game.manager.GameManager;
import game.minigioco.MontacarichiManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MontacarichiPanel extends BasePanel {
    private static final int DIMENSIONE_NODO = 90;
    private static final int[][] POSIZIONI_NODI = {
            {560, 260}, {680, 620}, {760, 980}
    };
    private static final int TIMER_PULSE_DELAY_MS = 40;
    private static final double PULSE_FREQUENCY_DIVISOR = 260.0;

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
    private final MontacarichiManager manager;
    private final Runnable onAvvio;

    private JPanel pannelloBarraTensione;
    private JButton btnColpisci;
    private JLabel bannerFase;
    private JPanel overlayAvvia;

    private final List<JButton> nodiAttivi = new ArrayList<>();
    private Timer timerPulseNodi; // solo estetico: alone pulsante

    // Cache di sola visualizzazione, aggiornata dagli eventi del manager
    private volatile int posizioneIndicatoreVista = 0;

    public MontacarichiPanel(
            GameManager gameManager,
            MontacarichiManager manager,
            PannelloSfondo sfondo,
            GestoreComponenti gestore,
            Runnable onAvvio
    ) {
        super(gameManager);
        this.manager = manager;
        this.sfondo = sfondo;
        this.gestore = gestore;
        this.onAvvio = onAvvio;

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
        // Delega pura: il Panel non decide se il colpo è riuscito
        bottone.addActionListener(e -> manager.onColpisci());
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
            onAvvio.run();
            manager.avviaMinigioco();
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

    /** Reagisce a MINIGIOCO_FASE_CAMBIATA = COMBATTENTE. */
    public void mostraFaseCombattente() {
        sfondo.setImmagineSfondo("/assets/Montacarichi.png");
        rimuoviNodi();

        pannelloBarraTensione.setVisible(true);
        btnColpisci.setVisible(true);
        bannerFase.setText(
                "<html><div style='text-align:center;'>Il Combattente tiene teso il cavo: premi <b>COLPISCI</b> "
                        + "quando l'indicatore è nella zona verde!</div></html>"
        );
        bannerFase.setVisible(true);
    }

    /** Reagisce a MINIGIOCO_FASE_CAMBIATA = NAVIGATRICE. */
    public void mostraFaseNavigatrice() {
        pannelloBarraTensione.setVisible(false);
        btnColpisci.setVisible(false);
        bannerFase.setText(
                "<html><div style='text-align:center;'>La Navigatrice calcola l'intreccio: clicca i 3 nodi "
                        + "nell'ordine numerico giusto (1 → 2 → 3).</div></html>"
        );
        creaNodi();
    }

    /** Reagisce a MINIGIOCO_INDICATORE_AGGIORNATO. Il chiamante (GUIObserver/listener)
     *  deve invocare questo metodo già dentro SwingUtilities.invokeLater, perché
     *  il tick arriva da un thread separato del manager. */
    public void aggiornaIndicatore(int posizione) {
        this.posizioneIndicatoreVista = posizione;
        pannelloBarraTensione.repaint();
    }

    /** Reagisce a MINIGIOCO_COLPO_ESITO: qui, e solo qui, si costruisce il testo per l'utente. */
    public void mostraEsitoColpo(MontacarichiManager.EsitoColpo esito, java.util.function.Consumer<String> mostraMessaggio) {
        if (esito.successo()) {
            mostraMessaggio.accept("Colpo riuscito! (" + esito.colpiRiusciti() + "/" + esito.colpiRichiesti() + ")");
        } else {
            mostraMessaggio.accept("Troppo presto o troppo tardi, riprova!");
        }
    }

    /** Reagisce a MINIGIOCO_NODO_ESITO. */
    public void mostraEsitoNodo(MontacarichiManager.EsitoNodo esito, java.util.function.Consumer<String> mostraMessaggio) {
        if (esito.corretto()) {
            if (esito.indice() < nodiAttivi.size()) {
                JButton nodo = nodiAttivi.get(esito.indice());
                nodo.setEnabled(false);
                nodo.repaint();
            }
        } else {
            mostraMessaggio.accept("Non è questo il nodo giusto!");
        }
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
                int max = manager.getIndicatoreMax();

                g2.setColor(COLORE_SFONDO_BARRA);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                int yZonaTop = h - (int) (h * (manager.getZonaVerdeMax() / (double) max));
                int yZonaBottom = h - (int) (h * (manager.getZonaVerdeMin() / (double) max));
                g2.setColor(COLORE_ZONA_VERDE);
                g2.fillRect(2, yZonaTop, w - 4, yZonaBottom - yZonaTop);

                int yIndicatore = h - (int) (h * (posizioneIndicatoreVista / (double) max));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, Math.max(0, Math.min(h - 6, yIndicatore - 3)), w, 6);

                g2.setColor(COLORE_BORDO_DORATO);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 16, 16);
                g2.dispose();
            }
        };
        pannello.setOpaque(false);
        return pannello;
    }

    private void creaNodi() {
        rimuoviNodi();

        for (int i = 0; i < MontacarichiManager.NUMERO_NODI; i++) {
            int indice = i;
            JButton nodo = creaNodo(i + 1);
            nodo.addActionListener(e -> manager.onNodoCliccato(indice));
            gestore.registra(nodo, POSIZIONI_NODI[i][0], POSIZIONI_NODI[i][1], DIMENSIONE_NODO, DIMENSIONE_NODO);
            nodiAttivi.add(nodo);
        }

        arrestaTimer(timerPulseNodi);
        timerPulseNodi = new Timer(TIMER_PULSE_DELAY_MS, e -> {
            for (JButton nodo : nodiAttivi) {
                if (nodo.isEnabled()) nodo.repaint();
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

                g2.setColor(coloreAlone);
                g2.fillOval(x - haloExtra, y - haloExtra, d + haloExtra * 2, d + haloExtra * 2);

                g2.setPaint(new GradientPaint(x, y, base.brighter(), x, y + d, base.darker()));
                g2.fillOval(x, y, d, d);

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

    private void rimuoviNodi() {
        arrestaTimer(timerPulseNodi);
        for (JButton nodo : nodiAttivi) {
            gestore.rimuovi(nodo);
        }
        nodiAttivi.clear();
    }

    private void arrestaTimer(Timer timer) {
        if (timer != null && timer.isRunning()) timer.stop();
    }

    /** Reagisce a MINIGIOCO_COMPLETATO. */
    public void nascondiTutto() {
        rimuoviNodi();
        bannerFase.setVisible(false);
    }

    @Override
    public void init() { }

    @Override
    public void aggiorna() { }

    public void reset() {
        rimuoviNodi();
        if (pannelloBarraTensione != null) pannelloBarraTensione.setVisible(false);
        if (btnColpisci != null) btnColpisci.setVisible(false);
        if (bannerFase != null) bannerFase.setVisible(false);
        if (overlayAvvia != null) overlayAvvia.setVisible(false);
        posizioneIndicatoreVista = 0;
    }
}