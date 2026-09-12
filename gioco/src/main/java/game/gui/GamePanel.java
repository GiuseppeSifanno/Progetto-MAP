package game.gui;

import engine.GUI.BasePanel;
import engine.model.BaseDialogo;
import engine.model.Battuta;
import engine.model.Personaggio;
import game.manager.GameManager;
import game.minigioco.MontacarichiManager;
import game.minigioco.ZuppaFogliantiManager;
import game.model.Atto;
import game.model.Dialogo;
import game.model.Interazione;
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

    /**
     * Un punto cliccabile della zona: id interazione + area sull'immagine originale.
     */
        private record Hotspot(String idInterazione, int centroX, int centroY, int larghezza, int altezza) {
    }
    
    private static class SpriteScena {
    final String percorsoImmagine;
    final String percorsoImmagineAlternativo;
    final String flagCondizione;
    final String flagNascondiSe;
    final String flagCompletato;
    final String idInterazione;
    final int centroX, centroY, larghezza, altezza;
    final double rotazione; // radianti, 0 = nessuna rotazione (immagine originale)

    SpriteScena(String percorsoImmagine, String idInterazione,
                int centroX, int centroY, int larghezza, int altezza) {
        this(percorsoImmagine, null, null, null, null, idInterazione,
                centroX, centroY, larghezza, altezza, 0.0);
    }

    SpriteScena(String percorsoImmagine, String percorsoImmagineAlternativo, String flagCondizione,
                String flagNascondiSe, String flagCompletato, String idInterazione,
                int centroX, int centroY, int larghezza, int altezza) {
        this(percorsoImmagine, percorsoImmagineAlternativo, flagCondizione, flagNascondiSe,
                flagCompletato, idInterazione, centroX, centroY, larghezza, altezza, 0.0);
    }

    // Costruttore completo
    SpriteScena(String percorsoImmagine, String percorsoImmagineAlternativo, String flagCondizione,
                String flagNascondiSe, String flagCompletato, String idInterazione,
                int centroX, int centroY, int larghezza, int altezza, double rotazione) {
        this.percorsoImmagine = percorsoImmagine;
        this.percorsoImmagineAlternativo = percorsoImmagineAlternativo;
        this.flagCondizione = flagCondizione;
        this.flagNascondiSe = flagNascondiSe;
        this.flagCompletato = flagCompletato;
        this.idInterazione = idInterazione;
        this.centroX = centroX;
        this.centroY = centroY;
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.rotazione = rotazione;
    }

    static SpriteScena raccoglibile(String percorsoImmagine, String idInterazione, String flagNascondiSe,
                                     int centroX, int centroY, int larghezza, int altezza) {
        return new SpriteScena(percorsoImmagine, null, null, flagNascondiSe, null,
                idInterazione, centroX, centroY, larghezza, altezza, 0.0);
    }

    static SpriteScena azioneUnica(String percorsoImmagine, String idInterazione, String flagCompletato,
                                    int centroX, int centroY, int larghezza, int altezza) {
        return new SpriteScena(percorsoImmagine, null, null, null, flagCompletato,
                idInterazione, centroX, centroY, larghezza, altezza, 0.0);
    }

    // Nuova variante: freccia/sprite statico ma ruotato di un angolo fisso
    static SpriteScena azioneUnicaRuotata(String percorsoImmagine, String idInterazione, String flagCompletato,
                                           int centroX, int centroY, int larghezza, int altezza, double rotazione) {
        return new SpriteScena(percorsoImmagine, null, null, null, flagCompletato,
                idInterazione, centroX, centroY, larghezza, altezza, rotazione);
    }

    static SpriteScena azioneConCambioImmagine(
            String percorsoImmagine, String percorsoImmagineAlternativo, String idInterazione,
            String flagCompletato, int centroX, int centroY, int larghezza, int altezza) {
        return new SpriteScena(percorsoImmagine, percorsoImmagineAlternativo, flagCompletato,
                null, flagCompletato, idInterazione, centroX, centroY, larghezza, altezza, 0.0);
    }
}



    private static final Map<String, String> ZONA_PER_ATTO = new HashMap<>();
    static {
        ZONA_PER_ATTO.put("a1", "spiaggia");
        ZONA_PER_ATTO.put("a2", "giungla");
        ZONA_PER_ATTO.put("a3", "miniera");
        ZONA_PER_ATTO.put("a4", "vulcano");
        ZONA_PER_ATTO.put("a5", "tesoro");
        // a0: nessuna zona con hotspot (introduzione)
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
        IMMAGINE_PER_ZONA.put("tesoro", "/assets/zone/Tesoro.png");
        IMMAGINE_PER_ZONA.put("spiaggiaest", "/assets/zone/SpiaggiaEst.png");
        IMMAGINE_PER_ZONA.put("spiaggiaovest", "/assets/zone/SpiaggiaOvest.png");
        IMMAGINE_PER_ZONA.put("entratagiungla", "/assets/zone/EntrataGiungla.png");
        IMMAGINE_PER_ZONA.put("giungla", "/assets/zone/Giungla.png");
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
                //new Hotspot("int_spiaggia_legnetti", 300, 700, 150, 150),
                //new Hotspot("int_spiaggia_navigatrice_lente", 600, 500, 150, 150),
                //new Hotspot("int_spiaggia_cespuglio", 900, 650, 150, 150),
                //new Hotspot("int_spiaggia_falo", 1100, 750, 150, 150),
                //new Hotspot("int_spiaggia_albero_cesto", 1300, 400, 150, 150),
                new Hotspot("int_spiaggia_combattente_cibo", 1450, 600, 150, 150)
                //new Hotspot("int_spiaggia_masso", 1550, 500, 150, 150),
                //new Hotspot("int_spiaggia_ingresso_giungla", 1600, 300, 150, 150)
        ));
        HOTSPOT_PER_ZONA.put("giungla", List.of(
                new Hotspot("int_giungla_fiume", 770, 780, 340, 220),
                new Hotspot("int_giungla_combattente_bastone_fiume", 1300, 300, 220, 160),
                new Hotspot("int_giungla_sentiero_foglianti", 768, 150, 260, 180),
                new Hotspot("int_giungla_capo_villaggio", 280, 650, 260, 200)
        ));
        HOTSPOT_PER_ZONA.put("miniera", List.of(
                new Hotspot("int_miniera_tunnel", 300, 500, 180, 180),
                new Hotspot("int_miniera_sassi", 225, 915, 130, 100),
                new Hotspot("int_miniera_bastone_spezzato", 95, 930, 160, 110),
                new Hotspot("int_miniera_calzino", 615, 955, 130, 100),
                new Hotspot("int_miniera_macchinari", 1400, 140, 180, 160),
                new Hotspot("int_miniera_uscita_vulcano", 768, 130, 220, 160)
        ));
        HOTSPOT_PER_ZONA.put("vulcano", List.of(
                new Hotspot("int_vulcano_liane", 600, 500, 200, 200),
                new Hotspot("int_vulcano_avvia_liane", 768, 130, 220, 160)
        ));
    }

    private static final Map<String, List<SpriteScena>> SPRITE_PER_ZONA = new HashMap<>();
    static {
        SPRITE_PER_ZONA.put("spiaggia", List.of(
                new SpriteScena("/assets/Personaggi/Capitano.png", null, 400, 550, 200, 300),
                SpriteScena.azioneUnica("/assets/Personaggi/Combattente.png", "int_spiaggia_combattente_cibo","o11", 800, 550, 200, 300),
                SpriteScena.azioneUnica("/assets/Personaggi/Navigatrice.png", "int_spiaggia_navigatrice_lente",
                        "f10", 600, 530, 200, 300),

                new SpriteScena("/assets/Oggetti/FuocoSpento.png", "/assets/Oggetti/FuocoAcceso.png",
                        "f1", null, "f1", "int_spiaggia_falo", 1100, 750, 150, 150)
        ));
        SPRITE_PER_ZONA.put("spiaggiaest", List.of(
                new SpriteScena("/assets/Oggetti/Legnetti.png", "int_spiaggia_legnetti", 350, 800, 220, 160)
        ));
        SPRITE_PER_ZONA.put("spiaggiaovest", List.of( 
                SpriteScena.azioneConCambioImmagine( "/assets/Oggetti/CespuglioConFoglieSecche.png", 
                    "/assets/Oggetti/Cespuglio.png", "int_spiaggia_cespuglio", "f9", 900, 650, 150, 150 )
        ));
        SPRITE_PER_ZONA.put("entratagiungla", List.of(
                SpriteScena.azioneConCambioImmagine("/assets/Oggetti/CestoPieno.png", "/assets/Oggetti/CestoVuoto.png", 
                    "int_spiaggia_albero_cesto", "f11", 1200, 200, 200, 200),
                SpriteScena.raccoglibile("/assets/Oggetti/GrandeMasso.png", "int_spiaggia_masso",
                        "f8", 800, 600, 275, 200),
                SpriteScena.azioneUnicaRuotata("/assets/Freccia.png", "int_spiaggia_ingresso_giungla", null,
                        836, 70, 90, 90, -Math.PI / 2)        ));
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
        private final JScrollPane scrollTesto;

        private static final String CARD_INDICATORE = "indicatore";
        private static final String CARD_SCELTE = "scelte";

        DialogBox() {
            setOpaque(false);
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

            lblNome.setFont(lblNome.getFont().deriveFont(Font.BOLD, 20f));
            lblNome.setForeground(COLORE_NOME);
            add(lblNome, BorderLayout.NORTH);

            txtTesto.setEditable(false);
            txtTesto.setFocusable(false);
            txtTesto.setOpaque(false);
            txtTesto.setLineWrap(true);
            txtTesto.setWrapStyleWord(true);
            txtTesto.setForeground(COLORE_TESTO);
            txtTesto.setFont(txtTesto.getFont().deriveFont(18f));

            scrollTesto = new JScrollPane(txtTesto);
            scrollTesto.setOpaque(false);
            scrollTesto.getViewport().setOpaque(false);
            scrollTesto.setBorder(BorderFactory.createEmptyBorder());
            scrollTesto.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollTesto.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            add(scrollTesto, BorderLayout.CENTER);

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
    private CardLayout cardOverlayPergamena;
    private JPanel cardsOverlayPergamena;
    private static final String CARD_BORSA = "borsa";
    private static final String CARD_PERGAMENA = "pergamena";
    private GestoreComponenti gestore;

    private ZuppaFogliantiPanel zuppaFogliantiPanel;
    private MontacarichiPanel montacarichiPanel;
    private LianePanel lianePanel;

    private final List<JButton> hotspotAttivi = new ArrayList<>();
    private final List<JButton> spriteAttivi = new ArrayList<>();
    private final Map<String, JButton> hotspotAttiviPerId = new HashMap<>();

    private Timer timerMessaggio;

    private static final int DURATA_BASE_MS = 1600;
    private static final int DURATA_PER_CARATTERE_MS = 35;
    private static final int DURATA_MAX_MS = 3000;

    private final List<JButton> frecceMovimento = new ArrayList<>();
    private String zonaCorrente;
    private Image immagineFrecciaBase;


    // hotspot che, al click, mostrano un popup "immagine + nome trovato"
    // (stesso stile delle erbe raccolte, riusabile in qualsiasi zona)
    private static final Map<String, String[]> POPUP_OGGETTO_HOTSPOT = new HashMap<>();
    static {
        POPUP_OGGETTO_HOTSPOT.put("int_miniera_sassi", new String[]{"/assets/pietra_focaia.png", "Pietra Focaia"});
        POPUP_OGGETTO_HOTSPOT.put("int_miniera_calzino", new String[]{"/assets/calzino.png", "Calzino"});
        POPUP_OGGETTO_HOTSPOT.put("int_miniera_bastone_spezzato", new String[]{"/assets/bastone_spezzato.png", "Bastone Spezzato"});
        POPUP_OGGETTO_HOTSPOT.put("int_miniera_macchinari", new String[]{"/assets/filo_acciaio.png", "Filo d'Acciaio"});
    }

    private static final List<String> HOTSPOT_USO_SINGOLO = List.of(
            "int_miniera_sassi", "int_miniera_calzino",
            "int_miniera_bastone_spezzato", "int_miniera_macchinari"
    );

    // i 3 hotspot che, insieme, danno gli ingredienti della torcia
    private static final List<String> INGREDIENTI_TORCIA = List.of(
            "int_miniera_sassi", "int_miniera_calzino", "int_miniera_bastone_spezzato"
    );

    private JPanel overlayOggettoTrovato;
    private JLabel immagineOggettoTrovato;
    private JLabel messaggioOggettoTrovato;
    private Timer timerOggettoTrovato;
    private JLabel bannerAvvisoCombina;
    private JPanel overlaySceltaFinale;
    private JPanel overlayImmagineFinale;
    private Runnable onContinuaImmagineFinale;


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

        zuppaFogliantiPanel = new ZuppaFogliantiPanel(
                gameManager, this, sfondo, gestore, this::preparaMinigioco
        );
        montacarichiPanel = new MontacarichiPanel(
                gameManager, this, sfondo, gestore, this::preparaMinigioco
        );
      
        lianePanel = new LianePanel(
                sfondo, gestore,
                this::preparaMinigioco,
                this::completaMinigiocoLiane,
                this::mostraMessaggio
        );
        
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
        gestore.registraCentrato(pergamenaOverlay, 1100, 800);
        pergamenaOverlay.setVisible(false);

        overlayOggettoTrovato = creaOverlayOggettoTrovato();
        sfondo.add(overlayOggettoTrovato);
        gestore.registraCentrato(overlayOggettoTrovato, 380, 420);
        overlayOggettoTrovato.setVisible(false);

        bannerAvvisoCombina = new JLabel("", SwingConstants.CENTER);
        bannerAvvisoCombina.setOpaque(true);
        bannerAvvisoCombina.setBackground(new Color(15, 15, 20, 210));
        bannerAvvisoCombina.setForeground(Color.WHITE);
        bannerAvvisoCombina.setFont(bannerAvvisoCombina.getFont().deriveFont(17f));
        bannerAvvisoCombina.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(20, new Color(255, 255, 255, 60)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        bannerAvvisoCombina.setVisible(false);
        sfondo.add(bannerAvvisoCombina);
        gestore.registraCentratoInBasso(bannerAvvisoCombina, 1100, 90, 20);

        overlaySceltaFinale = new JPanel(new GridLayout(1, 0, 40, 0));
        overlaySceltaFinale.setOpaque(false);
        sfondo.add(overlaySceltaFinale);
        gestore.registraCentrato(overlaySceltaFinale, 1300, 600);
        overlaySceltaFinale.setVisible(false);

        overlayImmagineFinale = creaOverlayImmagineFinale();
        sfondo.add(overlayImmagineFinale);
        gestore.registraCentrato(overlayImmagineFinale, 3000, 3000);
        overlayImmagineFinale.setVisible(false);
    }

    /** Popup con l'immagine dell'epilogo scelto (Dovere.png/Avidita.png), clicca per continuare. */
    private JPanel creaOverlayImmagineFinale() {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setOpaque(false);
        pannello.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel suggerimento = new JLabel("clicca per continuare", SwingConstants.CENTER);
        suggerimento.setOpaque(true);
        suggerimento.setBackground(new Color(20, 15, 10, 200));
        suggerimento.setForeground(new Color(240, 220, 190));
        suggerimento.setFont(suggerimento.getFont().deriveFont(Font.ITALIC, 15f));
        suggerimento.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel wrapperSuggerimento = new JPanel();
        wrapperSuggerimento.setOpaque(false);
        wrapperSuggerimento.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        wrapperSuggerimento.add(suggerimento);
        pannello.add(wrapperSuggerimento, BorderLayout.SOUTH);

        MouseAdapter chiudi = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pannello.setVisible(false);
                if (onContinuaImmagineFinale != null) {
                    Runnable proseguire = onContinuaImmagineFinale;
                    onContinuaImmagineFinale = null;
                    proseguire.run();
                }
            }
        };
        pannello.addMouseListener(chiudi);

        return pannello;
    }

    /** Cambia lo sfondo con l'immagine dell'epilogo scelto; al click prosegue con onContinua. */
    private void mostraImmagineFinale(String assetPath, Runnable onContinua) {
        sfondo.setImmagineSfondo(assetPath);
        onContinuaImmagineFinale = onContinua;

        sfondo.setComponentZOrder(overlayImmagineFinale, 0);
        overlayImmagineFinale.setVisible(true);
        overlayImmagineFinale.revalidate();
        overlayImmagineFinale.repaint();
    }

    /** Mostra le scelte come 2 (o più) "carte" grandi affiancate, invece dei bottoni stretti nel box dialogo. */
    private void mostraSceltaFinale(List<Scelta> scelte, java.util.function.IntConsumer onScelta) {
        overlaySceltaFinale.removeAll();

        for (int i = 0; i < scelte.size(); i++) {
            int indice = i;
            String testo = scelte.get(i).getTesto();
            String assetFinale = (i == 0) ? "/assets/Dovere.png" : "/assets/Avidita.png";
            JPanel card = creaCardScelta(testo, () -> {
                overlaySceltaFinale.setVisible(false);
                mostraImmagineFinale(assetFinale, () -> onScelta.accept(indice));
            });
            overlaySceltaFinale.add(card);
        }

        sfondo.setComponentZOrder(overlaySceltaFinale, 0);
        overlaySceltaFinale.setVisible(true);
        overlaySceltaFinale.revalidate();
        overlaySceltaFinale.repaint();
    }

    private JPanel creaCardScelta(String testo, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(24, new Color(198, 156, 109)),
                BorderFactory.createEmptyBorder(28, 24, 28, 24)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String testoEscapato = testo
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        JLabel testoLabel = new JLabel(
                "<html><div style='text-align:center; width: 340px;'>" + testoEscapato + "</div></html>",
                SwingConstants.CENTER
        );
        testoLabel.setForeground(new Color(240, 220, 190));
        testoLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 17));
        card.add(testoLabel, BorderLayout.CENTER);

        MouseAdapter click = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        };
        card.addMouseListener(click);
        testoLabel.addMouseListener(click);

        return card;
    }

    /** Mostra la schermata di chiusura a fine gioco (dopo l'epilogo dell'Atto 5), con il bottone per uscire. */
    private void mostraSchermataFine() {
        JPanel pannello = new JPanel(new BorderLayout(0, 20)) {
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
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel titolo = new JLabel("FINE", SwingConstants.CENTER);
        titolo.setFont(caricaFontAntico(40f));
        titolo.setForeground(new Color(240, 220, 190));
        pannello.add(titolo, BorderLayout.NORTH);

        JButton btnEsci = new JButton("ESCI DAL GIOCO");
        btnEsci.setFont(caricaFontAntico(22f));
        btnEsci.setForeground(new Color(240, 220, 190));
        btnEsci.setContentAreaFilled(false);
        btnEsci.setBorderPainted(false);
        btnEsci.setFocusPainted(false);
        btnEsci.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEsci.setHorizontalAlignment(SwingConstants.CENTER);
        btnEsci.addActionListener(e -> gameManager.stop());

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.add(btnEsci);
        pannello.add(wrapper, BorderLayout.CENTER);

        sfondo.add(pannello);
        gestore.registraCentrato(pannello, 520, 260);
        sfondo.setComponentZOrder(pannello, 0);
        pannello.setVisible(true);
        pannello.revalidate();
        pannello.repaint();
    }

    /** Mostra un messaggio transitorio in basso. Ignora null/stringhe vuote,
     *  e accoda i messaggi invece di interromperli a vicenda. */
    /** Mostra un messaggio transitorio in basso. Ignora null/stringhe vuote.
    *  Se un messaggio è già visibile, viene sostituito immediatamente da
    *  quello nuovo (nessuna coda, nessuna attesa). */
    public void mostraMessaggio(String messaggio) {
        if (messaggio == null || messaggio.isBlank()) return;

        etichettaMessaggio.setText(messaggio);
        etichettaMessaggio.setVisible(true);

        if (timerMessaggio != null && timerMessaggio.isRunning()) {
        timerMessaggio.stop();
        }

        // Durata proporzionale alla lunghezza del testo, con un tetto massimo.
        int durata = Math.min(DURATA_MAX_MS, DURATA_BASE_MS + messaggio.length() * DURATA_PER_CARATTERE_MS);

        timerMessaggio = new Timer(durata, e -> etichettaMessaggio.setVisible(false));
        timerMessaggio.setRepeats(false);
        timerMessaggio.start();
   }

    // ==================== Banner condiviso (unico in tutto il gioco) ====================

    /** Banner persistente in basso, condiviso da tutte le zone e i minigiochi.
     *  Essendo un unico componente, due banner non possono mai sovrapporsi:
     *  il secondo sostituisce semplicemente il primo. */
    public void mostraBanner(String testoHtml) {
        if (testoHtml == null || testoHtml.isBlank()) return;
        bannerAvvisoCombina.setText(testoHtml);
        sfondo.setComponentZOrder(bannerAvvisoCombina, 0);
        bannerAvvisoCombina.setVisible(true);
    }

    public void nascondiBanner() {
        bannerAvvisoCombina.setVisible(false);
    }

    // ==================== Popup "oggetto trovato" condiviso ====================

    /** Popup immagine+testo, si chiude da solo. Usato sia per gli hotspot della
     *  miniera sia dalla fase Navigatrice della zuppa: un solo overlay, mai due
     *  nello stesso punto. */
    /** Variante con dimensione anche del pannello contenitore (es. fiori: pannello più piccolo). */
    public void mostraPopupOggetto(String assetPath, String testo, int maxW, int maxH, int panelW, int panelH) {
        mostraOggettoTrovato(assetPath, testo, maxW, maxH, panelW, panelH);
    }

    /** Rimuove tutti gli hotspot della miniera tranne la freccia di uscita. */
    private void mostraSoloUscitaMiniera() {
        for (String id : new ArrayList<>(hotspotAttiviPerId.keySet())) {
            if (!"int_miniera_uscita_vulcano".equals(id)) {
                JButton bottone = hotspotAttiviPerId.remove(id);
                if (bottone != null) {
                    gestore.rimuovi(bottone);
                    hotspotAttivi.remove(bottone);
                }
            }
        }
    }

    /** Dopo il minigioco delle liane, toglie l'hotspot del groviglio (ormai risolto): resta solo la freccia. */
    private void nascondiLianeHotspot() {
        JButton bottone = hotspotAttiviPerId.remove("int_vulcano_liane");
        if (bottone != null) {
            gestore.rimuovi(bottone);
            hotspotAttivi.remove(bottone);
        }
    }

    /** Banner generico in basso, persistente finché non cambi zona: usato per "hai tutto, ora combina". */
    private void mostraBannerAvviso(String testoHtml) {
        bannerAvvisoCombina.setText(testoHtml);
        sfondo.setComponentZOrder(bannerAvvisoCombina, 0);
        bannerAvvisoCombina.setVisible(true);
    }

    /** Popup generico: immagine + "{nome} trovato", si chiude da solo dopo 1.8s. */
    private JPanel creaOverlayOggettoTrovato() {
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

        immagineOggettoTrovato = new JLabel("", SwingConstants.CENTER);
        pannello.add(immagineOggettoTrovato, BorderLayout.CENTER);

        messaggioOggettoTrovato = new JLabel("", SwingConstants.CENTER);
        messaggioOggettoTrovato.setForeground(new Color(240, 220, 190));
        messaggioOggettoTrovato.setFont(messaggioOggettoTrovato.getFont().deriveFont(Font.BOLD, 18f));
        pannello.add(messaggioOggettoTrovato, BorderLayout.SOUTH);

        return pannello;
    }

    /** Mostra il popup generico "oggetto trovato" con l'asset e il nome indicati.
    *  maxW/maxH: dimensione massima dell'immagine. panelW/panelH: dimensione
    *  del pannello contenitore (così i popup piccoli, es. fiori, hanno anche
    *  una cornice più piccola e non solo un'immagine più piccola dentro
    *  una cornice enorme). */
    private void mostraOggettoTrovato(String assetPath, String nome, int maxW, int maxH, int panelW, int panelH) {
        immagineOggettoTrovato.setIcon(caricaIconaAsset(assetPath, maxW, maxH));
        messaggioOggettoTrovato.setText(nome + " trovato");

        gestore.registraCentrato(overlayOggettoTrovato, panelW, panelH);

        sfondo.setComponentZOrder(overlayOggettoTrovato, 0);
        overlayOggettoTrovato.setVisible(true);
        overlayOggettoTrovato.revalidate();
        overlayOggettoTrovato.repaint();

        if (timerOggettoTrovato != null && timerOggettoTrovato.isRunning()) {
            timerOggettoTrovato.stop();
        }
        timerOggettoTrovato = new Timer(2400, e -> overlayOggettoTrovato.setVisible(false));
        timerOggettoTrovato.setRepeats(false);
        timerOggettoTrovato.start();
    }


    /**
     * Prepara GamePanel alla visualizzazione di un minigioco.
     * I minigiochi poi gestiscono autonomamente la propria interfaccia.
     */
    private void preparaMinigioco() {
        rimuoviHotspotAttuali();
        rimuoviFrecceMovimento();
        dialogBox.setVisible(false);
        zonaCorrente = null;
    }

    // ==================== Minigiochi: Zuppa Foglianti ====================

    public void mostraBottoneIniziaMinigioco() {
        zuppaFogliantiPanel.mostraBottoneIniziaMinigioco();
    }

    public void avviaFaseRaccoltaErbe() {
        zuppaFogliantiPanel.avviaFaseRaccoltaErbe();
    }

    public void mostraEsitoErba(ZuppaFogliantiManager.EsitoErba esito) {
        zuppaFogliantiPanel.mostraEsitoErba(esito);
    }

    public void mostraZuppaCompletata() {
        zuppaFogliantiPanel.mostraZuppaCompletata();
    }

// ==================== Minigiochi: Montacarichi ====================

    public void mostraBottoneAvviaMontacarichi() {
        montacarichiPanel.mostraBottoneAvviaMontacarichi();
    }

    public void mostraFaseCombattenteMontacarichi() {
        preparaMinigioco();
        montacarichiPanel.mostraFaseCombattente();
    }

    public void mostraFaseNavigatriceMontacarichi() {
        montacarichiPanel.mostraFaseNavigatrice();
    }

    public void aggiornaIndicatoreMontacarichi(int posizione) {
        montacarichiPanel.aggiornaIndicatore(posizione);
    }

    public void mostraEsitoColpoMontacarichi(MontacarichiManager.EsitoColpo esito) {
        montacarichiPanel.mostraEsitoColpo(esito);
    }

    public void mostraEsitoNodoMontacarichi(MontacarichiManager.EsitoNodo esito) {
        montacarichiPanel.mostraEsitoNodo(esito);
    }

    /** Chiamato quando MINIGIOCO_COMPLETATO arriva con payload del montacarichi. */
    public void completaMontacarichiUI() {
        montacarichiPanel.nascondiTutto();
        aggiorna(); // ricrea sprite/hotspot della miniera con lo stato aggiornato
        mostraSoloUscitaMiniera();
    }

    private void completaMinigiocoLiane() {
        gameManager.impostaFlag("f5");
        gameManager.getInterazioneObserver().tentaInterazione("int_vulcano_liane");
        aggiorna();
        nascondiLianeHotspot();
    }

    /** Carica un'immagine da /assets ridimensionata mantenendo le proporzioni. */
    public static ImageIcon caricaIconaAsset(String percorso, int maxW, int maxH) {
        java.net.URL risorsa = GamePanel.class.getResource(percorso);
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

    /** Cerca un font decorativo con fallback su Serif. */
    public static Font caricaFontAntico(float dimensione) {
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

        // ===== card 2: la pergamena =====
        JPanel cardPergamena = new JPanel(new BorderLayout(0, 10));
        cardPergamena.setOpaque(false);
        cardPergamena.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardPergamena.addMouseListener(chiudi);

        JLabel immaginePergamena = new JLabel(caricaIconaAsset("/assets/Pergamena.png", 1050, 700), SwingConstants.CENTER);
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
    /** Mostra al centro dello schermo la borsa recuperata dal fiume (con bottone "Apri" verso la pergamena). */
    public void mostraPergamena() {
        cardOverlayPergamena.show(cardsOverlayPergamena, CARD_BORSA);
        sfondo.setComponentZOrder(pergamenaOverlay, 0);
        pergamenaOverlay.setVisible(true);
        pergamenaOverlay.revalidate();
        pergamenaOverlay.repaint();
    }

    /** Nasconde il banner generico di avviso (es. dopo aver creato con successo la torcia). */
    public void nascondiBannerAvviso() {
        nascondiBanner();
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
        ricreaSprite(idZona); 
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
        ricreaSprite(nuovaZona);
        creaFrecceMovimento(nuovaZona);

        revalidate();
        repaint();
    }

    private void rimuoviHotspotAttuali() {
        for (JButton b : hotspotAttivi) {
            gestore.rimuovi(b);
        }
        hotspotAttivi.clear();
        hotspotAttiviPerId.clear();
    }
    
    private void rimuoviSpriteAttuali() {
        for (JButton b : spriteAttivi) {
            gestore.rimuovi(b);
        }
        spriteAttivi.clear();
    }

    private void ricreaSprite(String idZona) {
    rimuoviSpriteAttuali();

    List<SpriteScena> sprite = SPRITE_PER_ZONA.getOrDefault(idZona, List.of());
    for (SpriteScena s : sprite) {

        if (s.flagNascondiSe != null
                && gameManager.getInventarioManager().hasOggetto(s.flagNascondiSe)) {
            continue;
        }

        String percorsoDaUsare = percorsoAttualeSprite(s);

        java.net.URL risorsa = getClass().getResource(percorsoDaUsare);
        if (risorsa == null) {
            System.err.println("GamePanel: risorsa non trovata: " + percorsoDaUsare);
            continue;
        }

        ImageIcon icona;
        if (s.rotazione != 0.0) {
            Image originale = new ImageIcon(risorsa).getImage();
            BufferedImage ruotata = ruotaImmagine(originale, s.rotazione);
            icona = new ImageIcon(ruotata);
        } else {
            icona = new ImageIcon(risorsa);
        }

        JButton bottoneSprite = new JButton(icona);

        boolean giaCompletato = s.flagCompletato != null
                && gameManager.getInventarioManager().hasOggetto(s.flagCompletato);

        if (s.idInterazione != null && !giaCompletato) {
            bottoneSprite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            bottoneSprite.addActionListener(e -> {
                gameManager.getInterazioneObserver()
                        .tentaInterazione(s.idInterazione);

                SwingUtilities.invokeLater(() -> {
                    if (zonaCorrente != null) {
                        ricreaSprite(zonaCorrente);
                    }
                });
            });
        }

        gestore.registra(bottoneSprite, s.centroX, s.centroY, s.larghezza, s.altezza);
        spriteAttivi.add(bottoneSprite);
    }
}

    /** Restituisce il percorso immagine corretto per lo sprite, in base al flag associato (se presente). */
    private String percorsoAttualeSprite(SpriteScena s) {

        // Se esiste una condizione per cambiare immagine
        if (s.flagCondizione != null
                && s.percorsoImmagineAlternativo != null) {

            boolean condizioneAttiva =
                    gameManager.getInventarioManager()
                            .hasOggetto(s.flagCondizione);

            if (condizioneAttiva) {
                return s.percorsoImmagineAlternativo;
            }
        }

        return s.percorsoImmagine;
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
    // GestoreComponenti li riadatta da solo a ogni resize.
    int dimensioneOriginale = 90;
    int margineOriginale = 25;

    double scalaX = sfondo.getScalaX();
    double scalaY = sfondo.getScalaY();
    Rectangle area = sfondo.getAreaImmagine();

    if (area.width <= 0 || area.height <= 0 || scalaX <= 0 || scalaY <= 0) {
        // Il pannello non ha ancora una dimensione valida: riprova più tardi ad avviare la funzione
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

        List<Hotspot> hotspot = HOTSPOT_PER_ZONA.getOrDefault(idZona, List.of());
        for (Hotspot h : hotspot) {
            JButton bottoneHotspot = new JButton();
            bottoneHotspot.setFocusPainted(false);
            bottoneHotspot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boolean isFrecciaSu = "int_giungla_sentiero_foglianti".equals(h.idInterazione)
                    || "int_miniera_uscita_vulcano".equals(h.idInterazione)
                    || "int_vulcano_avvia_liane".equals(h.idInterazione);
            boolean isAvviaLiane = "int_vulcano_avvia_liane".equals(h.idInterazione);

            bottoneHotspot.addActionListener(e -> {
                // Verifica ESPLICITA prima di applicare qualunque conseguenza sulla UI:
                // la rimozione dell'hotspot deve dipendere dal fatto che l'interazione
                // sia stata davvero applicata, non dal solo click.
                Interazione interazione = gameManager.getInterazioneObserver()
                        .getInterazioni().get(h.idInterazione);
                boolean eraSbloccata = interazione != null
                        && interazione.getCondizioni().stream()
                        .allMatch(gameManager.getGameState().getInventario()::hasOggetto);
                if (isAvviaLiane) {
                    // Prima del minigioco: mostra il bottone per avviarlo.
                    // Dopo (o17 già ottenuto): la stessa freccia fa scattare
                    // l'interazione del tesoro, che porta all'Atto 5.
                    if (gameManager.getGameState().getInventario().hasOggetto("f5")) {
                        gameManager.getInterazioneObserver().tentaInterazione("int_vulcano_tesoro");
                    } else {
                        lianePanel.mostraBottoneAvvia();
                    }
                    return;
                }

                gameManager.getInterazioneObserver().tentaInterazione(h.idInterazione);

                String[] popup = POPUP_OGGETTO_HOTSPOT.get(h.idInterazione);
                if (popup != null && eraSbloccata) {
                    mostraOggettoTrovato(popup[0], popup[1], 280, 280, 380, 420);
                    gestore.rimuovi(bottoneHotspot);
                    hotspotAttivi.remove(bottoneHotspot);
                    hotspotAttiviPerId.remove(h.idInterazione);
                }

                if (HOTSPOT_USO_SINGOLO.contains(h.idInterazione) && popup == null && eraSbloccata) {
                    gestore.rimuovi(bottoneHotspot);
                    hotspotAttivi.remove(bottoneHotspot);
                    hotspotAttiviPerId.remove(h.idInterazione);
                }

                if (INGREDIENTI_TORCIA.contains(h.idInterazione)) {
                    var inventario = gameManager.getGameState().getInventario();
                    if (inventario.hasOggetto("o6") && inventario.hasOggetto("o7") && inventario.hasOggetto("o8")) {
                        mostraBanner(
                                "<html><div style='text-align:center;'>Hai tutto il necessario per la torcia!<br>"
                                        + "Apri l'inventario (tasto E) e usa <b>Combina</b> per crearla.</div></html>"
                        );
                    }
                }

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

            if (isFrecciaSu) {
                // Icona impostata DOPO registra(): così GestoreComponenti non la
                // cattura per il rescaling automatico "a copertura" (che con un
                // box non quadrato taglierebbe la freccia). Dimensione
                // fissa 110x110, centrata nel box grazie all'allineamento
                // di default del JButton.
                bottoneHotspot.setIcon(creaIconaFrecciaRuotata("NORD", 110));
                bottoneHotspot.setContentAreaFilled(false);
                bottoneHotspot.setBorderPainted(false);
                bottoneHotspot.setOpaque(false);
            }



            // TODO TEST: rimetti a true (o scommenta) per rendere di nuovo visibili gli hotspot di debug
            boolean debugHotspotVisibili = false;

            if (debugHotspotVisibili && !isFrecciaSu) {
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
            hotspotAttiviPerId.put(h.idInterazione, bottoneHotspot);
        }
    }

    // ==================== Dialogo stile visual novel ====================

    /**
     * Riceve il dialogo corrente (di solito da un evento DIALOGO_CAMBIATO) e
     * riparte dalla prima battuta.
     */
    public void aggiornaDialogo(BaseDialogo dialogo) {
        if (dialogo == null) {
            String attoCorrente = gameManager.getGameState().getIdAttoCorrente();
            boolean eraD3Atto2 = dialogoCorrente != null
                    && "d3".equals(dialogoCorrente.getId())
                    && "a2".equals(attoCorrente);

            // Corretto: il prompt "avvia minigioco" è la battuta finale di d3
            // (dopo che il giocatore ha recuperato i fili d'acciaio dai macchinari),
            // non di d2.
            boolean eraD3Atto3 = dialogoCorrente != null
                    && "d3".equals(dialogoCorrente.getId())
                    && "a3".equals(attoCorrente);

            // Fine del gioco: d2 o d3 dell'Atto 5 sono i due epiloghi narrativi
            // (Dovere/Avidità), entrambi senza nextId. Qualunque dei due sia
            // appena finito, mostra la schermata di chiusura.
            boolean eraFineGioco = dialogoCorrente != null
                    && ("d2".equals(dialogoCorrente.getId()) || "d3".equals(dialogoCorrente.getId()))
                    && "a5".equals(attoCorrente);

            dialogBox.svuota();
            dialogoCorrente = null;
            battuteCorrenti = List.of();
            indiceBattuta = 0;
            dialogBox.setVisible(false);
            dialogBox.revalidate();
            dialogBox.repaint();

            if (eraD3Atto2) {
                mostraBottoneIniziaMinigioco();
            }
            if (eraD3Atto3) {
                // Guardia: non mostrare il bottone se il montacarichi è già stato
                // riparato (es. macchinari ri-cliccato dopo il completamento).
                if (!gameManager.getInventarioManager().hasOggetto("f4")) {
                    mostraBottoneAvviaMontacarichi();
                }
            }
            // eraD3Atto3-come-"congratulazioni" ELIMINATO: quel banner lo mostra
            // già MontacarichiPanel.nascondiTutto() al vero completamento, non
            // qui alla sola fine di un dialogo di narrazione.
            if (eraFineGioco) {
                mostraSchermataFine();
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
        //reimposto lo stile a quello normale
        dialogBox.txtTesto.setFont(dialogBox.txtTesto.getFont().deriveFont(Font.PLAIN, 18f));

        if (nome.equals("Nessuno")) {
            nome = "";
            //mostro il testo con un font diverso
            dialogBox.txtTesto.setFont(dialogBox.txtTesto.getFont().deriveFont(Font.ITALIC, 20f));
        }
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
            String attoCorrente = gameManager.getGameState().getIdAttoCorrente();
            if ("a5".equals(attoCorrente)) {
                mostraSceltaFinale(
                        dialogoConcreto.getScelte(),
                        indice -> gameManager.getDialogManager().scegliOpzione(indice)
                );
            } else {
                dialogBox.mostraScelte(
                        dialogoConcreto.getScelte(),
                        indice -> gameManager.getDialogManager().scegliOpzione(indice)
                );
            }
            return;
        }
        gameManager.getDialogManager().prossimoDialogo();
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
        rimuoviSpriteAttuali(); 
        rimuoviFrecceMovimento();

        if (timerMessaggio != null && timerMessaggio.isRunning()) {
            timerMessaggio.stop();
        }
        etichettaMessaggio.setVisible(false);

        dialogBox.setVisible(false);

        zonaCorrente = null;

        pergamenaOverlay.setVisible(false);
        bannerAvvisoCombina.setVisible(false);
        overlaySceltaFinale.setVisible(false);
        overlayImmagineFinale.setVisible(false);

        zuppaFogliantiPanel.reset();
        montacarichiPanel.reset();
        lianePanel.reset();
    }
}