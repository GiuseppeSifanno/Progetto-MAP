package game.gui;

import engine.model.BaseDialogo;
import engine.model.Battuta;
import engine.model.Personaggio;
import game.manager.GameManager;
import game.minigioco.ZuppaFogliantiManager;
import game.model.Atto;
import game.model.Dialogo;
import game.model.Scelta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;

/**
 * Schermata di gioco: mostra lo sfondo della zona corrente, gli hotspot
 * cliccabili per interagire con la zona e un box dialogo in stile
 * "visual novel" (una battuta alla volta, click per avanzare, scelte
 * integrate nello stesso box quando il dialogo finisce).
 */
public class GamePanel extends BasePanel {

    /** Un punto cliccabile della zona: id interazione + area sull'immagine originale. */
    private static class Hotspot {
        final String idInterazione;
        final int centroX, centroY, larghezza, altezza;

        Hotspot(String idInterazione, int centroX, int centroY, int larghezza, int altezza) {
            this.idInterazione = idInterazione;
            this.centroX = centroX;
            this.centroY = centroY;
            this.larghezza = larghezza;
            this.altezza = altezza;
        }
    }

    /** Un'erba/radice cliccabile nella fase Navigatrice del minigioco della zuppa. */
    private static class ErbaHotspot {
        final String idErba;
        final String nome;
        final String assetIcona;
        final int centroX, centroY, larghezza, altezza;

        ErbaHotspot(String idErba, String nome, String assetIcona, int centroX, int centroY, int larghezza, int altezza) {
            this.idErba = idErba;
            this.nome = nome;
            this.assetIcona = assetIcona;
            this.centroX = centroX;
            this.centroY = centroY;
            this.larghezza = larghezza;
            this.altezza = altezza;
        }
    }

    // I 7 elementi raccoglibili nella fase Navigatrice (4 buoni, 3 velenosi),
    // devono corrispondere agli id in GameManager.configZuppa.
    private static final List<ErbaHotspot> ERBE_RACCOGLIBILI = List.of(
            new ErbaHotspot("erba1", "Fiori Gialli", "/assets/Erba.png", 670, 500, 110, 110),
            new ErbaHotspot("erba2", "Fiori Viola", "/assets/Erba.png", 870, 745, 110, 110),
            new ErbaHotspot("erba3", "Fiori Azzurri", "/assets/Erba.png", 1075, 640, 110, 110),
            new ErbaHotspot("erba4", "Bacche Rosse", "/assets/Erba.png", 985, 705, 110, 110),
            new ErbaHotspot("erba5", "Funghi Chiazzati", "/assets/Erba.png", 205, 665, 130, 110),
            new ErbaHotspot("erba6", "Radice Contorta", "/assets/Radici.png", 560, 615, 120, 100),
            new ErbaHotspot("erba7", "Radice Nodosa", "/assets/Radici.png", 1290, 560, 120, 100)
    );


    private static final Map<String, String> ZONA_PER_ATTO = new HashMap<>();
    static {
        ZONA_PER_ATTO.put("a1", "spiaggia");
        ZONA_PER_ATTO.put("a2", "giungla");
        ZONA_PER_ATTO.put("a3", "miniera");
        ZONA_PER_ATTO.put("a4", "vulcano");
        // a0 e a5: nessuna zona con hotspot (introduzione/finale)
    }

    // idZona -> percorso immagine di sfondo
    private static final Map<String, String> IMMAGINE_PER_ZONA = new HashMap<>();
    static {
        IMMAGINE_PER_ZONA.put("introduzione", "/assets/zone/Introduzione.png");
        IMMAGINE_PER_ZONA.put("spiaggia", "/assets/zone/Spiaggia.png");
        IMMAGINE_PER_ZONA.put("giungla", "/assets/zone/Giungla.png");
        IMMAGINE_PER_ZONA.put("campofoglianti", "/assets/zone/CampoFoglianti.png");
        IMMAGINE_PER_ZONA.put("miniera", "/assets/zone/Miniera.png");
        IMMAGINE_PER_ZONA.put("vulcano", "/assets/zone/Vulcano.png");
        IMMAGINE_PER_ZONA.put("spiaggiaest", "/assets/zone/SpiaggiaEst.png");
        IMMAGINE_PER_ZONA.put("spiaggiaovest", "/assets/zone/SpiaggiaOvest.png");
        IMMAGINE_PER_ZONA.put("entratagiungla", "/assets/zone/EntrataGiungla.png");
    }
    
    private static final Map<String, Map<String, String>> MOVIMENTI_PER_ZONA = new HashMap<>();
    static {
        Map<String, String> spiaggia = new HashMap<>();
        spiaggia.put("EST", "spiaggiaest");
        spiaggia.put("OVEST", "spiaggiaovest");

        Map<String, String> spiaggiaEst = new HashMap<>();
        spiaggiaEst.put("OVEST", "spiaggia");

        Map<String, String> spiaggiaOvest = new HashMap<>();
        spiaggiaOvest.put("EST", "spiaggia");
        spiaggiaOvest.put("NORD", "entratagiungla");

        Map<String, String> entrataGiungla = new HashMap<>();
        entrataGiungla.put("SUD", "spiaggiaovest");

        MOVIMENTI_PER_ZONA.put("spiaggia", spiaggia);
        MOVIMENTI_PER_ZONA.put("spiaggiaest", spiaggiaEst);
        MOVIMENTI_PER_ZONA.put("spiaggiaovest", spiaggiaOvest);
        MOVIMENTI_PER_ZONA.put("entratagiungla", entrataGiungla);
    }
    
    private static final Map<String, Double> ANGOLO_PER_DIREZIONE = new HashMap<>();
    static {
        // Freccia.png punta di default verso EST (destra).
        ANGOLO_PER_DIREZIONE.put("EST", 0.0);
        ANGOLO_PER_DIREZIONE.put("SUD", Math.PI / 2);
        ANGOLO_PER_DIREZIONE.put("OVEST", Math.PI);
        ANGOLO_PER_DIREZIONE.put("NORD", -Math.PI / 2);
    }

    // idZona -> hotspot, con gli id REALI presi dai file JSON delle zone.
    // Coordinate PROVVISORIE, da misurare sulle immagini vere.
    private static final Map<String, List<Hotspot>> HOTSPOT_PER_ZONA = new HashMap<>();
    static {
        HOTSPOT_PER_ZONA.put("spiaggia", List.of(
                new Hotspot("int_spiaggia_legnetti", 300, 700, 150, 150),
                new Hotspot("int_spiaggia_navigatrice_lente", 600, 500, 150, 150),
                new Hotspot("int_spiaggia_cespuglio", 900, 650, 150, 150),
                new Hotspot("int_spiaggia_falo", 1100, 750, 150, 150),
                new Hotspot("int_spiaggia_albero_cesto", 1300, 400, 150, 150),
                new Hotspot("int_spiaggia_combattente_cibo", 1450, 600, 150, 150),
                new Hotspot("int_spiaggia_masso", 1550, 500, 150, 150),
                new Hotspot("int_spiaggia_ingresso_giungla", 1600, 300, 150, 150)
        ));
        HOTSPOT_PER_ZONA.put("giungla", List.of(
                new Hotspot("int_giungla_fiume", 770, 780, 340, 220),
                new Hotspot("int_giungla_combattente_bastone_fiume", 280, 650, 260, 200),
                new Hotspot("int_giungla_sentiero_foglianti", 768, 150, 260, 180),
                new Hotspot("int_giungla_capo_villaggio", 1300, 300, 220, 160)
        ));
        HOTSPOT_PER_ZONA.put("miniera", List.of(
                new Hotspot("int_miniera_tunnel", 300, 500, 180, 180),
                new Hotspot("int_miniera_sassi", 550, 700, 180, 180),
                new Hotspot("int_miniera_bastone_spezzato", 800, 600, 180, 180),
                new Hotspot("int_miniera_torcia", 1000, 400, 180, 180),
                new Hotspot("int_miniera_macchinari", 1250, 550, 180, 180),
                new Hotspot("int_miniera_montacarichi", 1450, 500, 180, 180),
                new Hotspot("int_miniera_uscita_vulcano", 1600, 350, 180, 180)
        ));
        HOTSPOT_PER_ZONA.put("vulcano", List.of(
                new Hotspot("int_vulcano_liane", 600, 500, 200, 200),
                new Hotspot("int_vulcano_tesoro", 1100, 450, 200, 200)
        ));
    }

    // ==================== Box dialogo (visual novel) ====================

    private static final Color COLORE_SFONDO_BOX = new Color(15, 15, 20, 210);
    private static final Color COLORE_BORDO_BOX = new Color(255, 255, 255, 60);
    private static final Color COLORE_NOME = new Color(255, 205, 90);
    private static final Color COLORE_TESTO = Color.WHITE;
    private static final Color COLORE_INDICATORE = new Color(255, 255, 255, 140);

    /** Box arrotondato con nome personaggio, testo battuta e, alternativamente, le scelte. */
    private class DialogBox extends JPanel {
        private final JLabel lblNome = new JLabel(" ");
        private final JTextArea txtTesto = new JTextArea();
        private final JLabel lblIndicatore = new JLabel("▼ clicca per continuare", SwingConstants.RIGHT);
        private final JPanel pannelloScelte = new JPanel();
        private final CardLayout cardSud = new CardLayout();
        private final JPanel sud = new JPanel(cardSud);

        private static final String CARD_INDICATORE = "indicatore";
        private static final String CARD_SCELTE = "scelte";

        DialogBox() {
            setOpaque(false);
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

            lblNome.setFont(lblNome.getFont().deriveFont(Font.BOLD, 22f));
            lblNome.setForeground(COLORE_NOME);
            add(lblNome, BorderLayout.NORTH);

            txtTesto.setEditable(false);
            txtTesto.setFocusable(false);
            txtTesto.setOpaque(false);
            txtTesto.setLineWrap(true);
            txtTesto.setWrapStyleWord(true);
            txtTesto.setForeground(COLORE_TESTO);
            txtTesto.setFont(txtTesto.getFont().deriveFont(20f));
            add(txtTesto, BorderLayout.CENTER);

            lblIndicatore.setForeground(COLORE_INDICATORE);
            lblIndicatore.setFont(lblIndicatore.getFont().deriveFont(Font.ITALIC, 15f));

            pannelloScelte.setOpaque(false);
            pannelloScelte.setLayout(new BoxLayout(pannelloScelte, BoxLayout.Y_AXIS));

            sud.setOpaque(false);
            sud.add(lblIndicatore, CARD_INDICATORE);
            sud.add(pannelloScelte, CARD_SCELTE);
            add(sud, BorderLayout.SOUTH);
            cardSud.show(sud, CARD_INDICATORE);

            MouseAdapter avanza = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("DialogBox: mouseClicked");
                    avanzaBattuta();
                }
            };
            // Il listener va sui componenti "coprenti" (testo/nome), non solo sul pannello,
            // altrimenti il click sopra di essi non arriverebbe al box.
            addMouseListener(avanza);
            txtTesto.addMouseListener(avanza);
            lblNome.addMouseListener(avanza);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void mostraBattuta(String nome, String testo) {
            lblNome.setText(nome == null || nome.isBlank() ? " " : nome);
            txtTesto.setText(testo);
            cardSud.show(sud, CARD_INDICATORE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void mostraScelte(List<Scelta> scelte, java.util.function.IntConsumer onScelta) {
            pannelloScelte.removeAll();

            for (int i = 0; i < scelte.size(); i++) {
                int indice = i;
                JButton bottone = new JButton(scelte.get(i).getTesto());
                bottone.setFocusPainted(false);
                bottone.setAlignmentX(Component.CENTER_ALIGNMENT);
                bottone.addActionListener(e -> onScelta.accept(indice));

                pannelloScelte.add(bottone);
                if (i < scelte.size() - 1) {
                    pannelloScelte.add(Box.createVerticalStrut(4));
                }
            }

            cardSud.show(sud, CARD_SCELTE);
            setCursor(Cursor.getDefaultCursor());

            pannelloScelte.revalidate();
            pannelloScelte.repaint();

            // Aggiorna la dimensione del box in base alle scelte
            SwingUtilities.invokeLater(() -> {
                Dimension dimensione = dialogBox.getPreferredSize();
                dialogBox.setPreferredSize(
                        new Dimension(1200, dimensione.height)
                );
                gestore.riposizionaTutti();
                dialogBox.revalidate();
                dialogBox.repaint();
            });
        }

        void svuota() {
            lblNome.setText(" ");
            txtTesto.setText("");
            cardSud.show(sud, CARD_INDICATORE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arco = 28;
            g2.setColor(COLORE_SFONDO_BOX);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
            g2.setColor(COLORE_BORDO_BOX);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private PannelloSfondo sfondo;
    private DialogBox dialogBox;
    private JLabel etichettaMessaggio;
    private JPanel pergamenaOverlay;
    private JPanel overlayIniziaMinigioco;
    private CardLayout cardOverlayPergamena;
    private JPanel cardsOverlayPergamena;
    private static final String CARD_BORSA = "borsa";
    private static final String CARD_PERGAMENA = "pergamena";
    private GestoreComponenti gestore;

    private final List<JButton> hotspotAttivi = new ArrayList<>();
    private Timer timerMessaggio;
    private final List<JButton> frecceMovimento = new ArrayList<>();
    private String zonaCorrente;
    private Image immagineFrecciaBase;

    // ==== Minigioco zuppa: fase Navigatrice (raccolta erbe/radici) ====
    private final Map<String, JButton> hotspotErbePerId = new HashMap<>();
    private JLabel contatoreErbe;
    private JLabel bannerObiettivoErbe;
    private JPanel overlayEsitoErba;
    private JLabel immagineEsitoErba;
    private JLabel messaggioEsitoErba;
    private Timer timerEsitoErba;
    private JPanel overlayCreaZuppa;
    private JPanel overlayZuppaCompletata;
    private JPanel overlayTransizioneSentiero;
    private int erbeCorretteRaccolte = 0;
    private static final int ERBE_CORRETTE_RICHIESTE = 4;

    // Stato di avanzamento battuta-per-battuta del dialogo corrente
    private BaseDialogo dialogoCorrente;
    private List<Battuta> battuteCorrenti = List.of();
    private int indiceBattuta = 0;

    public GamePanel(GameManager gameManager) {
        super(gameManager);
        setLayout(new BorderLayout());
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        sfondo = new PannelloSfondo("/assets/Menu.png");
        add(sfondo, BorderLayout.CENTER);

        gestore = new GestoreComponenti(sfondo);
        
        dialogBox = new DialogBox();
        sfondo.add(dialogBox);

        etichettaMessaggio = new JLabel("", SwingConstants.CENTER);
        etichettaMessaggio.setOpaque(true);
        etichettaMessaggio.setBackground(new Color(0, 0, 0, 200));
        etichettaMessaggio.setForeground(Color.YELLOW);
        etichettaMessaggio.setVisible(false);
        sfondo.add(etichettaMessaggio);

        // Box dialogo: centrato orizzontalmente e ancorato in basso
        gestore.registraCentratoInBasso(
                dialogBox,
                1300,
                150,
                20
        );

        gestore.registra(
                etichettaMessaggio,
                836,
                470,
                800,
                60
        );

        pergamenaOverlay = creaPergamenaOverlay();
        sfondo.add(pergamenaOverlay);
        gestore.registraCentrato(pergamenaOverlay, 480, 620);
        pergamenaOverlay.setVisible(false);

        overlayIniziaMinigioco = creaOverlayIniziaMinigioco();
        sfondo.add(overlayIniziaMinigioco);
        gestore.registraCentrato(overlayIniziaMinigioco, 520, 220);
        overlayIniziaMinigioco.setVisible(false);

        // ==== Minigioco zuppa: fase Navigatrice ====
        contatoreErbe = new JLabel("", SwingConstants.CENTER);
        contatoreErbe.setOpaque(true);
        contatoreErbe.setBackground(new Color(20, 15, 10, 220));
        contatoreErbe.setForeground(new Color(240, 220, 190));
        contatoreErbe.setFont(contatoreErbe.getFont().deriveFont(Font.BOLD, 20f));
        contatoreErbe.setBorder(new BordoArrotondato(14, new Color(198, 156, 109)));
        contatoreErbe.setVisible(false);
        sfondo.add(contatoreErbe);
        // in alto a destra: coordinate vicine al bordo destro dell'immagine originale (1536x1024)
        gestore.registra(contatoreErbe, 1400, 70, 240, 60);

        bannerObiettivoErbe = new JLabel(
                "<html><div style='text-align:center;'>Trova le erbe e le radici commestibili per la zuppa: alcune sono velenose!<br>"
                        + "Raccoglile finché non ne hai " + ERBE_CORRETTE_RICHIESTE + " buone.</div></html>",
                SwingConstants.CENTER
        );
        bannerObiettivoErbe.setOpaque(true);
        bannerObiettivoErbe.setBackground(new Color(15, 15, 20, 210));
        bannerObiettivoErbe.setForeground(Color.WHITE);
        bannerObiettivoErbe.setFont(bannerObiettivoErbe.getFont().deriveFont(17f));
        bannerObiettivoErbe.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(20, new Color(255, 255, 255, 60)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        bannerObiettivoErbe.setVisible(false);
        sfondo.add(bannerObiettivoErbe);
        gestore.registraCentratoInBasso(bannerObiettivoErbe, 1100, 90, 20);

        overlayEsitoErba = creaOverlayEsitoErba();
        sfondo.add(overlayEsitoErba);
        gestore.registraCentrato(overlayEsitoErba, 380, 420);
        overlayEsitoErba.setVisible(false);

        overlayCreaZuppa = creaOverlayCreaZuppa();
        sfondo.add(overlayCreaZuppa);
        gestore.registraCentrato(overlayCreaZuppa, 520, 220);
        overlayCreaZuppa.setVisible(false);

        overlayZuppaCompletata = creaOverlayZuppaCompletata();
        sfondo.add(overlayZuppaCompletata);
        gestore.registraCentrato(overlayZuppaCompletata, 420, 460);
        overlayZuppaCompletata.setVisible(false);

        overlayTransizioneSentiero = creaOverlayTransizioneSentiero();
        sfondo.add(overlayTransizioneSentiero);
        gestore.registraCentrato(overlayTransizioneSentiero, 900, 320);
        overlayTransizioneSentiero.setVisible(false);
    }

    /** Pannello con il bottone "CREA ZUPPA", stesso stile del bottone inizia minigioco. */
    private JPanel creaOverlayCreaZuppa() {
        JPanel pannello = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 15, 10, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JButton btnCrea = new JButton("CREA ZUPPA");
        btnCrea.setFont(caricaFontAntico(30f));
        btnCrea.setForeground(new Color(240, 220, 190));
        btnCrea.setContentAreaFilled(false);
        btnCrea.setBorderPainted(false);
        btnCrea.setFocusPainted(false);
        btnCrea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCrea.setHorizontalAlignment(SwingConstants.CENTER);
        btnCrea.addActionListener(e -> {
            overlayCreaZuppa.setVisible(false);
            gameManager.creaZuppaMinigioco();
        });

        pannello.add(btnCrea, BorderLayout.CENTER);
        return pannello;
    }

    /** Pannello con zuppa.png, mostrato a fine minigioco. */
    private JPanel creaOverlayZuppaCompletata() {
        JPanel pannello = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 15, 10, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(16, 16, 12, 16)
        ));

        JLabel immagine = new JLabel(caricaIconaAsset("/assets/zuppa.png", 370, 370), SwingConstants.CENTER);
        pannello.add(immagine, BorderLayout.CENTER);

        JLabel messaggio = new JLabel("La zuppa è pronta!", SwingConstants.CENTER);
        messaggio.setForeground(new Color(240, 220, 190));
        messaggio.setFont(messaggio.getFont().deriveFont(Font.BOLD, 18f));
        pannello.add(messaggio, BorderLayout.SOUTH);

        return pannello;
    }

    /** Popup con l'immagine (Erba.png/Radici.png) e l'esito velenosa/commestibile. */
    private JPanel creaOverlayEsitoErba() {
        JPanel pannello = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 15, 10, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(16, 16, 12, 16)
        ));

        immagineEsitoErba = new JLabel("", SwingConstants.CENTER);
        pannello.add(immagineEsitoErba, BorderLayout.CENTER);

        messaggioEsitoErba = new JLabel("", SwingConstants.CENTER);
        messaggioEsitoErba.setFont(messaggioEsitoErba.getFont().deriveFont(Font.BOLD, 18f));
        pannello.add(messaggioEsitoErba, BorderLayout.SOUTH);

        return pannello;
    }

    /**
     * Mostra la schermata della fase Navigatrice: cambia sfondo a
     * RaccogliErbe.png e crea gli hotspot per i 7 elementi raccoglibili.
     */
    public void avviaFaseRaccoltaErbe() {
        // Ripulisce eventuali hotspot/frecce della zona di gioco normale
        rimuoviHotspotAttuali();
        rimuoviFrecceMovimento();
        dialogBox.setVisible(false);
        overlayIniziaMinigioco.setVisible(false);
        zonaCorrente = null;

        erbeCorretteRaccolte = 0;
        aggiornaContatoreErbe();

        sfondo.setImmagineSfondo("/assets/RaccogliErbe.png");

        rimuoviHotspotErbe();

        // TODO TEST: commenta questa riga (o mettila a false) per nascondere i box di debug
        boolean debugVisibile = true;

        for (ErbaHotspot eh : ERBE_RACCOGLIBILI) {
            JButton bottone = new JButton();
            bottone.setFocusPainted(false);
            bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            bottone.addActionListener(e -> gameManager.selezionaErba(eh.idErba));

            gestore.registra(bottone, eh.centroX, eh.centroY, eh.larghezza, eh.altezza);

            if (debugVisibile) {
                bottone.setContentAreaFilled(true);
                bottone.setBorderPainted(true);
                bottone.setOpaque(true);
                bottone.setBackground(new Color(0, 200, 0, 90));
                bottone.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 0), 2));
                bottone.setForeground(Color.WHITE);
                bottone.setFont(bottone.getFont().deriveFont(Font.BOLD, 11f));
                bottone.setText(eh.nome);
            } else {
                bottone.setContentAreaFilled(false);
                bottone.setBorderPainted(false);
                bottone.setOpaque(false);
            }

            hotspotErbePerId.put(eh.idErba, bottone);
        }

        contatoreErbe.setVisible(true);
        bannerObiettivoErbe.setVisible(true);

        revalidate();
        repaint();
    }

    private void rimuoviHotspotErbe() {
        for (JButton b : hotspotErbePerId.values()) {
            gestore.rimuovi(b);
        }
        hotspotErbePerId.clear();
    }

    private void rimuoviHotspotErbaPerId(String idErba) {
        JButton b = hotspotErbePerId.remove(idErba);
        if (b != null) {
            gestore.rimuovi(b);
        }
    }

    private void aggiornaContatoreErbe() {
        contatoreErbe.setText("Erbe buone: " + erbeCorretteRaccolte + "/" + ERBE_CORRETTE_RICHIESTE);
    }

    /** Esito del click su un'erba/radice: mostra il popup con immagine e messaggio, e rimuove l'hotspot cliccato. */
    public void mostraEsitoErba(ZuppaFogliantiManager.EsitoErba esito) {
        ErbaHotspot trovata = null;
        for (ErbaHotspot eh : ERBE_RACCOGLIBILI) {
            if (eh.idErba.equals(esito.idErba())) {
                trovata = eh;
                break;
            }
        }

        String nome = trovata != null ? trovata.nome : esito.idErba();
        String asset = trovata != null ? trovata.assetIcona : "/assets/Erba.png";

        immagineEsitoErba.setIcon(caricaIconaAsset(asset, 280, 280));

        if (esito.corretta()) {
            messaggioEsitoErba.setText(nome + ": commestibile! Aggiunta al cesto.");
            messaggioEsitoErba.setForeground(new Color(150, 230, 150));
            erbeCorretteRaccolte++;
            aggiornaContatoreErbe();
        } else {
            messaggioEsitoErba.setText(nome + ": velenosa! Meglio lasciarla.");
            messaggioEsitoErba.setForeground(new Color(230, 130, 130));
        }

        boolean raccoltaCompletata = erbeCorretteRaccolte >= ERBE_CORRETTE_RICHIESTE;

        // Rimuove l'hotspot corrispondente, così non è ricliccabile
        rimuoviHotspotErbaPerId(esito.idErba());

        sfondo.setComponentZOrder(overlayEsitoErba, 0);
        overlayEsitoErba.setVisible(true);
        overlayEsitoErba.revalidate();
        overlayEsitoErba.repaint();

        if (timerEsitoErba != null && timerEsitoErba.isRunning()) {
            timerEsitoErba.stop();
        }
        timerEsitoErba = new Timer(1800, e -> {
            overlayEsitoErba.setVisible(false);
            if (raccoltaCompletata) {
                mostraBottoneCreaZuppa();
            }
        });
        timerEsitoErba.setRepeats(false);
        timerEsitoErba.start();
    }

    /** Mostra il bottone "CREA ZUPPA", una volta raccolte tutte le erbe/radici buone. */
    private void mostraBottoneCreaZuppa() {
        sfondo.setComponentZOrder(overlayCreaZuppa, 0);
        overlayCreaZuppa.setVisible(true);
        overlayCreaZuppa.revalidate();
        overlayCreaZuppa.repaint();
    }

    /** Mostra zuppa.png a fine minigioco, poi ripulisce la schermata e sblocca il passaggio all'Atto 3. */
    public void mostraZuppaCompletata() {
        sfondo.setComponentZOrder(overlayZuppaCompletata, 0);
        overlayZuppaCompletata.setVisible(true);
        overlayZuppaCompletata.revalidate();
        overlayZuppaCompletata.repaint();

        Timer timerChiusura = new Timer(2200, e -> {
            overlayZuppaCompletata.setVisible(false);
            concludiMinigiocoZuppa();
        });
        timerChiusura.setRepeats(false);
        timerChiusura.start();
    }

    /**
     * Ripulisce la schermata di raccolta erbe e mostra la transizione con
     * Sentiero.png, prima di completare l'interazione
     * "int_giungla_capo_villaggio" (che fa scattare PROSSIMO_ATTO).
     */
    private void concludiMinigiocoZuppa() {
        contatoreErbe.setVisible(false);
        bannerObiettivoErbe.setVisible(false);
        rimuoviHotspotErbe();

        mostraTransizioneSentiero();
    }

    /** Schermata di passaggio (sfondo Sentiero.png) tra la zuppa e l'Atto 3. */
    private void mostraTransizioneSentiero() {
        sfondo.setImmagineSfondo("/assets/Sentiero.png");
        sfondo.setComponentZOrder(overlayTransizioneSentiero, 0);
        overlayTransizioneSentiero.setVisible(true);
        overlayTransizioneSentiero.revalidate();
        overlayTransizioneSentiero.repaint();
    }

    /** Pannello semitrasparente con il testo di passaggio, sovrapposto a Sentiero.png. */
    private JPanel creaOverlayTransizioneSentiero() {
        JPanel pannello = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 10, 10, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createEmptyBorder(30, 40, 24, 40));
        pannello.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel testo = new JLabel(
                "<html><div style='text-align:center;'>La ciurma saluta i Foglianti e si incammina lungo il "
                        + "sentiero indicato dal Capo, verso l'ingresso della miniera.</div></html>",
                SwingConstants.CENTER
        );
        testo.setFont(new Font(Font.SERIF, Font.ITALIC, 22));
        testo.setForeground(new Color(232, 226, 214));
        pannello.add(testo, BorderLayout.CENTER);

        JLabel suggerimento = new JLabel("clicca per continuare", SwingConstants.CENTER);
        suggerimento.setForeground(new Color(138, 133, 120));
        suggerimento.setFont(suggerimento.getFont().deriveFont(Font.ITALIC, 13f));
        pannello.add(suggerimento, BorderLayout.SOUTH);

        MouseAdapter prosegui = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                overlayTransizioneSentiero.setVisible(false);
                gameManager.getInterazioneObserver().tentaInterazione("int_giungla_capo_villaggio");
            }
        };
        pannello.addMouseListener(prosegui);
        testo.addMouseListener(prosegui);

        return pannello;
    }

    /** Pannello con il bottone "INIZIA MINIGIOCO", in un font in stile "antico/pirata". */
    private JPanel creaOverlayIniziaMinigioco() {
        JPanel pannello = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 15, 10, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JButton btnInizia = new JButton("INIZIA MINIGIOCO");
        btnInizia.setFont(caricaFontAntico(30f));
        btnInizia.setForeground(new Color(240, 220, 190));
        btnInizia.setContentAreaFilled(false);
        btnInizia.setBorderPainted(false);
        btnInizia.setFocusPainted(false);
        btnInizia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInizia.setHorizontalAlignment(SwingConstants.CENTER);
        btnInizia.addActionListener(e -> {
            overlayIniziaMinigioco.setVisible(false);
            gameManager.avviaMinigiocoZuppa();
        });

        pannello.add(btnInizia, BorderLayout.CENTER);
        return pannello;
    }

    /**
     * Cerca un font dall'aspetto "antico/pirata" tra quelli installati nel
     * sistema, con fallback su un Serif grassetto-corsivo se nessuno è
     * disponibile (i font decorativi non sono garantiti su ogni piattaforma).
     */
    private Font caricaFontAntico(float dimensione) {
        String[] candidati = {"Papyrus", "Herculanum", "Luminari", "Trattatello", "Copperplate"};
        List<String> disponibili = List.of(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        );
        for (String nome : candidati) {
            if (disponibili.contains(nome)) {
                return new Font(nome, Font.BOLD, (int) dimensione);
            }
        }
        return new Font(Font.SERIF, Font.BOLD | Font.ITALIC, (int) dimensione);
    }

    /** Mostra il bottone centrale per avviare il minigioco della zuppa. */
    public void mostraBottoneIniziaMinigioco() {
        sfondo.setComponentZOrder(overlayIniziaMinigioco, 0);
        overlayIniziaMinigioco.setVisible(true);
        overlayIniziaMinigioco.revalidate();
        overlayIniziaMinigioco.repaint();
    }

    /** Pannello con due schermate: borsa (con bottone "Apri") e pergamena. */
    private JPanel creaPergamenaOverlay() {
        JPanel pannello = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 15, 10, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(18, 18, 12, 18)
        ));

        cardOverlayPergamena = new CardLayout();
        cardsOverlayPergamena = new JPanel(cardOverlayPergamena);
        cardsOverlayPergamena.setOpaque(false);

        MouseAdapter chiudi = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pannello.setVisible(false);
            }
        };

        // ===== card 1: la borsa, con il bottone "Apri" =====
        JPanel cardBorsa = new JPanel(new BorderLayout(0, 14));
        cardBorsa.setOpaque(false);

        JLabel immagineBorsa = new JLabel(caricaIconaAsset("/assets/borsa.png", 380, 420), SwingConstants.CENTER);
        immagineBorsa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        immagineBorsa.addMouseListener(chiudi);
        cardBorsa.add(immagineBorsa, BorderLayout.CENTER);

        JButton btnApri = new JButton("Apri");
        btnApri.setFont(btnApri.getFont().deriveFont(Font.BOLD, 16f));
        btnApri.addActionListener(e -> cardOverlayPergamena.show(cardsOverlayPergamena, CARD_PERGAMENA));
        JPanel wrapperBottone = new JPanel();
        wrapperBottone.setOpaque(false);
        wrapperBottone.add(btnApri);
        cardBorsa.add(wrapperBottone, BorderLayout.SOUTH);

        // ===== card 2: la pergamena dei Foglianti =====
        JPanel cardPergamena = new JPanel(new BorderLayout(0, 10));
        cardPergamena.setOpaque(false);
        cardPergamena.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardPergamena.addMouseListener(chiudi);

        JLabel immaginePergamena = new JLabel(caricaIconaAsset("/assets/PergamenaFoglianti.png", 430, 500), SwingConstants.CENTER);
        immaginePergamena.addMouseListener(chiudi);
        cardPergamena.add(immaginePergamena, BorderLayout.CENTER);

        JLabel suggerimento = new JLabel("clicca per chiudere", SwingConstants.CENTER);
        suggerimento.setForeground(new Color(240, 220, 190));
        suggerimento.setFont(suggerimento.getFont().deriveFont(Font.ITALIC, 13f));
        cardPergamena.add(suggerimento, BorderLayout.SOUTH);

        cardsOverlayPergamena.add(cardBorsa, CARD_BORSA);
        cardsOverlayPergamena.add(cardPergamena, CARD_PERGAMENA);

        pannello.add(cardsOverlayPergamena, BorderLayout.CENTER);

        return pannello;
    }

    /** Carica un'immagine da /assets ridimensionata mantenendo le proporzioni. */
    private ImageIcon caricaIconaAsset(String percorso, int maxW, int maxH) {
        java.net.URL risorsa = getClass().getResource(percorso);
        if (risorsa == null) {
            System.err.println("GamePanel: immagine non trovata: " + percorso);
            return new ImageIcon();
        }
        Image originale = new ImageIcon(risorsa).getImage();
        int larghezzaOriginale = originale.getWidth(null);
        int altezzaOriginale = originale.getHeight(null);
        if (larghezzaOriginale <= 0 || altezzaOriginale <= 0) {
            return new ImageIcon(originale);
        }
        double scala = Math.min((double) maxW / larghezzaOriginale, (double) maxH / altezzaOriginale);
        int w = (int) (larghezzaOriginale * scala);
        int h = (int) (altezzaOriginale * scala);
        return new ImageIcon(originale.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /** Mostra al centro dello schermo la borsa recuperata dal fiume (con bottone "Apri" verso la pergamena). */
    public void mostraPergamena() {
        cardOverlayPergamena.show(cardsOverlayPergamena, CARD_BORSA);
        sfondo.setComponentZOrder(pergamenaOverlay, 0);
        pergamenaOverlay.setVisible(true);
        pergamenaOverlay.revalidate();
        pergamenaOverlay.repaint();
    }

    // ==================== Zona / hotspot ====================

    /** Cambia sfondo e hotspot in base all'id atto (es. "a1" -> zona "spiaggia"). */
    public void aggiornaImmagine(String idAtto) {

        String idZona = ZONA_PER_ATTO.get(idAtto);

        if (idZona == null) {
            rimuoviHotspotAttuali();
            rimuoviFrecceMovimento();
            return;
        }

        zonaCorrente = idZona;

        String immagine = IMMAGINE_PER_ZONA.get(idZona);

        if (immagine != null) {
            sfondo.setImmagineSfondo(immagine);
        } else {
            System.err.println(
                    "GamePanel: nessuna immagine registrata per la zona: "
                            + idZona
            );
        }

        ricreaHotspot(idZona);

        creaFrecceMovimento(idZona);
    }
    
    private void cambiaZona(String nuovaZona) {
        if (nuovaZona == null) {
            return;
        }

        zonaCorrente = nuovaZona;

        String immagine = IMMAGINE_PER_ZONA.get(nuovaZona);

        if (immagine == null) {
            System.err.println(
                    "GamePanel: nessuna immagine per la zona " + nuovaZona
            );
            return;
        }

        sfondo.setImmagineSfondo(immagine);

        ricreaHotspot(nuovaZona);

        creaFrecceMovimento(nuovaZona);

        revalidate();
        repaint();
    }

    private void rimuoviHotspotAttuali() {
        for (JButton b : hotspotAttivi) {
            gestore.rimuovi(b);
        }
        hotspotAttivi.clear();
    }
    
    private void rimuoviFrecceMovimento() {
        for (JButton freccia : frecceMovimento) {
            gestore.rimuovi(freccia);
        }

        frecceMovimento.clear();
    }
    
    /**
    * Restituisce l'icona della freccia già ruotata per la direzione indicata.
    * Freccia.png punta di default verso EST (destra).
    */
   private ImageIcon creaIconaFrecciaRuotata(String direzione) {
       if (immagineFrecciaBase == null) {
           immagineFrecciaBase = new ImageIcon(getClass().getResource("/assets/Freccia.png")).getImage();
       }

       double angolo = ANGOLO_PER_DIREZIONE.getOrDefault(direzione, 0.0);
       BufferedImage ruotata = ruotaImmagine(immagineFrecciaBase, angolo);
       return new ImageIcon(ruotata);
   }

   /**
    * Come {@link #creaIconaFrecciaRuotata(String)}, ma ridimensionata a una
    * dimensione FISSA (quadrata), mantenendo le proporzioni senza tagli.
    * Utile per hotspot con un'area di click più grande dell'icona stessa
    * (es. hotspot non quadrati), dove il ridimensionamento automatico di
    * GestoreComponenti farebbe un "cover crop" indesiderato.
    */
   private ImageIcon creaIconaFrecciaRuotata(String direzione, int dimensione) {
       Image base = creaIconaFrecciaRuotata(direzione).getImage();
       Image scalata = base.getScaledInstance(dimensione, dimensione, Image.SCALE_SMOOTH);
       return new ImageIcon(scalata);
   }
    
    private void creaFrecceMovimento(String idZona) {
    rimuoviFrecceMovimento();

    Map<String, String> movimenti =
            MOVIMENTI_PER_ZONA.getOrDefault(idZona, Map.of());

    if (movimenti.isEmpty()) {
        return;
    }

    // Dimensione e margine in coordinate dell'immagine ORIGINALE:
    // GestoreComponenti li riscala da solo ad ogni resize.
    int dimensioneOriginale = 90;
    int margineOriginale = 25;

    double scalaX = sfondo.getScalaX();
    double scalaY = sfondo.getScalaY();
    Rectangle area = sfondo.getAreaImmagine();

    if (area.width <= 0 || area.height <= 0 || scalaX <= 0 || scalaY <= 0) {
        // Il pannello non ha ancora una dimensione valida: riprova più tardi
        SwingUtilities.invokeLater(() -> creaFrecceMovimento(idZona));
        return;
    }

    // Dimensioni dell'immagine originale, ricavate dall'area scalata attuale
    int larghezzaOriginale = (int) Math.round(area.width / scalaX);
    int altezzaOriginale = (int) Math.round(area.height / scalaY);

    for (Map.Entry<String, String> movimento : movimenti.entrySet()) {
        String direzione = movimento.getKey();
        String destinazione = movimento.getValue();

        JButton freccia = new JButton();
        
        freccia.setIcon(creaIconaFrecciaRuotata(direzione));

        freccia.setContentAreaFilled(false);
        freccia.setBorderPainted(false);
        freccia.setFocusPainted(false);
        freccia.setOpaque(false);
        freccia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        freccia.addActionListener(e -> cambiaZona(destinazione));

        int centroX;
        int centroY;

        switch (direzione) {
            case "NORD":
                centroX = larghezzaOriginale / 2;
                centroY = margineOriginale + dimensioneOriginale / 2;
                break;
            case "SUD":
                centroX = larghezzaOriginale / 2;
                centroY = altezzaOriginale - margineOriginale - dimensioneOriginale / 2;
                break;
            case "EST":
                centroX = larghezzaOriginale - margineOriginale - dimensioneOriginale / 2;
                centroY = altezzaOriginale / 2;
                break;
            case "OVEST":
                centroX = margineOriginale + dimensioneOriginale / 2;
                centroY = altezzaOriginale / 2;
                break;
            default:
                continue;
        }

        gestore.registra(freccia, centroX, centroY, dimensioneOriginale, dimensioneOriginale);
        frecceMovimento.add(freccia);
    }
}
    
    private BufferedImage ruotaImmagine(Image immagine, double angolo) {
        int larghezza = immagine.getWidth(null);
        int altezza = immagine.getHeight(null);

        BufferedImage risultato = new BufferedImage(
                larghezza,
                altezza,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = risultato.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g2.rotate(
                angolo,
                larghezza / 2.0,
                altezza / 2.0
        );

        g2.drawImage(
                immagine,
                0,
                0,
                null
        );

        g2.dispose();

        return risultato;
    }

    private void ricreaHotspot(String idZona) {
        rimuoviHotspotAttuali();

        // TODO TEST: commenta questa riga (o mettila a false) per tornare agli hotspot invisibili
        boolean debugHotspotVisibili = true;

        List<Hotspot> hotspot = HOTSPOT_PER_ZONA.getOrDefault(idZona, List.of());
        for (Hotspot h : hotspot) {
            JButton bottoneHotspot = new JButton();
            bottoneHotspot.setFocusPainted(false);
            bottoneHotspot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boolean isSentieroFoglianti = "int_giungla_sentiero_foglianti".equals(h.idInterazione);

            bottoneHotspot.addActionListener(e -> {
                gameManager.getInterazioneObserver().tentaInterazione(h.idInterazione);

                // Sentiero verso il villaggio dei Foglianti: se l'interazione è
                // sbloccata (borsa già recuperata), si passa alla nuova schermata.
                if ("int_giungla_sentiero_foglianti".equals(h.idInterazione)
                        && gameManager.getGameState().getInventario().hasOggetto("o5")) {
                    cambiaZona("campofoglianti");
                }
            });

            // registra() imposta il bottone come trasparente (pensato per gli
            // hotspot "normali"): lo stile di debug va applicato DOPO, altrimenti
            // viene sovrascritto.
            gestore.registra(bottoneHotspot, h.centroX, h.centroY, h.larghezza, h.altezza);

            if (isSentieroFoglianti) {
                // Icona impostata DOPO registra(): così GestoreComponenti non la
                // cattura per il rescaling automatico "a copertura" (che con un
                // box non quadrato 260x180 taglierebbe la freccia). Dimensione
                // fissa 110x110, centrata nel box grazie all'allineamento
                // di default del JButton.
                bottoneHotspot.setIcon(creaIconaFrecciaRuotata("NORD", 110));
                bottoneHotspot.setContentAreaFilled(false);
                bottoneHotspot.setBorderPainted(false);
                bottoneHotspot.setOpaque(false);
            }

            if (debugHotspotVisibili && !isSentieroFoglianti) {
                bottoneHotspot.setContentAreaFilled(true);
                bottoneHotspot.setBorderPainted(true);
                bottoneHotspot.setOpaque(true);
                bottoneHotspot.setBackground(new Color(255, 0, 0, 100));
                bottoneHotspot.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                bottoneHotspot.setForeground(Color.WHITE);
                bottoneHotspot.setFont(bottoneHotspot.getFont().deriveFont(Font.BOLD, 11f));
                bottoneHotspot.setText(h.idInterazione);
            }

            hotspotAttivi.add(bottoneHotspot);
        }
    }

    // ==================== Dialogo stile visual novel ====================

    /**
     * Riceve il dialogo corrente (di solito da un evento DIALOGO_CAMBIATO) e
     * riparte dalla prima battuta.
     */
    public void aggiornaDialogo(BaseDialogo dialogo) {
        if (dialogo == null) {
            // Se il dialogo che si è appena chiuso era "d3" (fine narrazione
            // Atto 2, subito prima del minigioco), mostra il bottone d'avvio.
            boolean eraD3 = dialogoCorrente != null && "d3".equals(dialogoCorrente.getId());

            dialogBox.svuota();
            dialogoCorrente = null;
            battuteCorrenti = List.of();
            indiceBattuta = 0;

            dialogBox.setVisible(false);

            dialogBox.revalidate();
            dialogBox.repaint();

            if (eraD3) {
                mostraBottoneIniziaMinigioco();
            }

            return;
        }

        // Il dialogo esiste
        dialogBox.setVisible(true);

        this.dialogoCorrente = dialogo;
        this.battuteCorrenti =
                dialogo.getBattute() != null
                        ? dialogo.getBattute()
                        : List.of();

        this.indiceBattuta = 0;

        if (battuteCorrenti.isEmpty()) {
            gestisciFineBattute();
        } else {
            mostraBattutaCorrente();
        }

        dialogBox.revalidate();
        dialogBox.repaint();
    }

    private void mostraBattutaCorrente() {
        Battuta battuta = battuteCorrenti.get(indiceBattuta);
        String nome = risolviNomePersonaggio(battuta.personaggioId());
        dialogBox.mostraBattuta(nome, battuta.testo() == null ? "" : battuta.testo().trim());
    }

    private String risolviNomePersonaggio(String idPersonaggio) {
        if (idPersonaggio == null || idPersonaggio.isBlank()) return null;
        Atto atto = (Atto) gameManager.getDialogManager().getAtto();
        if (atto == null) return idPersonaggio;
        Personaggio p = atto.getPersonaggio(idPersonaggio);
        return p != null ? p.getNome() : idPersonaggio;
    }

    /** Chiamato dal click sul box: avanza alla battuta successiva o gestisce la fine del dialogo. */
    private void avanzaBattuta() {
        if (indiceBattuta < battuteCorrenti.size() - 1) {
            indiceBattuta++;
            mostraBattutaCorrente();
        } else {
            gestisciFineBattute();
        }
    }

    /** Terminate le battute: mostra le scelte se presenti, altrimenti avanza al dialogo successivo. */
    private void gestisciFineBattute() {
        if (dialogoCorrente instanceof Dialogo dialogoConcreto
                && dialogoConcreto.getNumeroScelte() > 0) {
            dialogBox.mostraScelte(
                    dialogoConcreto.getScelte(),
                    indice -> gameManager.getDialogManager().scegliOpzione(indice)
            );
            return;
        }
        gameManager.getDialogManager().prossimoDialogo();
    }

    /** Mostra temporaneamente un messaggio (bloccato/sbloccato) al centro schermo. */
    public void mostraMessaggio(String messaggio) {
        etichettaMessaggio.setText(messaggio);
        etichettaMessaggio.setVisible(true);

        if (timerMessaggio != null && timerMessaggio.isRunning()) {
            timerMessaggio.stop();
        }
        timerMessaggio = new Timer(2500, e -> etichettaMessaggio.setVisible(false));
        timerMessaggio.setRepeats(false);
        timerMessaggio.start();
    }

    // ==================== Ciclo di vita BasePanel ====================

    @Override
    public void init() {
        // Pull-based: quando la schermata sta per essere mostrata, legge lo
        // stato attuale del gioco invece di aspettare solo eventi futuri
        // (stesso pattern di InventarioPanel.init()).
        aggiorna();
    }

    @Override
    public void aggiorna() {
        String idAtto = gameManager.getGameState().getIdAttoCorrente();

        if (idAtto != null) {
            aggiornaImmagine(idAtto);
        }

        BaseDialogo dialogo = gameManager.getDialogManager().getDialogo();

        if (dialogo != null) {
            aggiornaDialogo(dialogo);
        } else {
            dialogBox.svuota();
            dialogoCorrente = null;
            battuteCorrenti = List.of();
            dialogBox.setVisible(false);
        }

        revalidate();
        repaint();
    }

    @Override
    public void reset() {
        dialogBox.svuota();
        dialogoCorrente = null;
        battuteCorrenti = List.of();
        indiceBattuta = 0;
        rimuoviHotspotAttuali();
        etichettaMessaggio.setVisible(false);
        dialogBox.setVisible(false);
        pergamenaOverlay.setVisible(false);
        overlayIniziaMinigioco.setVisible(false);
        if (cardOverlayPergamena != null) {
            cardOverlayPergamena.show(cardsOverlayPergamena, CARD_BORSA);
        }
        rimuoviFrecceMovimento();
        zonaCorrente = null;

        rimuoviHotspotErbe();
        contatoreErbe.setVisible(false);
        bannerObiettivoErbe.setVisible(false);
        overlayEsitoErba.setVisible(false);
        overlayCreaZuppa.setVisible(false);
        overlayZuppaCompletata.setVisible(false);
        overlayTransizioneSentiero.setVisible(false);
        erbeCorretteRaccolte = 0;
    }
}