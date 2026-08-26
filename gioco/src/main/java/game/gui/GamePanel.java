package game.gui;

import engine.model.BaseDialogo;
import engine.model.Battuta;
import engine.model.Personaggio;
import game.manager.GameManager;
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

    // idAtto -> idZona (mirror di GameManager.ZONE_PER_ATTO)
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
        IMMAGINE_PER_ZONA.put("spiaggia", "/assets/zone/Spiaggia.png");
        IMMAGINE_PER_ZONA.put("giungla", "/assets/zone/Giungla.png");
        IMMAGINE_PER_ZONA.put("miniera", "/assets/zone/Miniera.png");
        IMMAGINE_PER_ZONA.put("vulcano", "/assets/zone/Vulcano.png");
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
                new Hotspot("int_giungla_fiume", 400, 600, 180, 180),
                new Hotspot("int_giungla_combattente_bastone_fiume", 700, 550, 180, 180),
                new Hotspot("int_giungla_sentiero_foglianti", 1000, 450, 180, 180),
                new Hotspot("int_giungla_capo_villaggio", 1300, 500, 180, 180)
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

            lblNome.setFont(lblNome.getFont().deriveFont(Font.BOLD, 17f));
            lblNome.setForeground(COLORE_NOME);
            add(lblNome, BorderLayout.NORTH);

            txtTesto.setEditable(false);
            txtTesto.setFocusable(false);
            txtTesto.setOpaque(false);
            txtTesto.setLineWrap(true);
            txtTesto.setWrapStyleWord(true);
            txtTesto.setForeground(COLORE_TESTO);
            txtTesto.setFont(txtTesto.getFont().deriveFont(15f));
            add(txtTesto, BorderLayout.CENTER);

            lblIndicatore.setForeground(COLORE_INDICATORE);
            lblIndicatore.setFont(lblIndicatore.getFont().deriveFont(Font.ITALIC, 12f));

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
    private GestoreComponenti gestore;

    private final List<JButton> hotspotAttivi = new ArrayList<>();
    private Timer timerMessaggio;

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
                900,
                130,
                20
        );

        gestore.registra(
                etichettaMessaggio,
                836,
                470,
                800,
                60
        );
    }

    // ==================== Zona / hotspot ====================

    /** Cambia sfondo e hotspot in base all'id atto (es. "a1" -> zona "spiaggia"). */
    public void aggiornaImmagine(String idAtto) {
        String idZona = ZONA_PER_ATTO.get(idAtto);

        if (idZona == null) {
            rimuoviHotspotAttuali();
            return;
        }

        String immagine = IMMAGINE_PER_ZONA.get(idZona);
        if (immagine != null) {
            sfondo.setImmagineSfondo(immagine);
        } else {
            System.err.println("GamePanel: nessuna immagine registrata per la zona: " + idZona);
        }

        ricreaHotspot(idZona);
    }

    private void rimuoviHotspotAttuali() {
        for (JButton b : hotspotAttivi) {
            gestore.rimuovi(b);
        }
        hotspotAttivi.clear();
    }

    private void ricreaHotspot(String idZona) {
        rimuoviHotspotAttuali();

        List<Hotspot> hotspot = HOTSPOT_PER_ZONA.getOrDefault(idZona, List.of());
        for (Hotspot h : hotspot) {
            JButton bottoneHotspot = new JButton();
            bottoneHotspot.setContentAreaFilled(false);
            bottoneHotspot.setBorderPainted(false);
            bottoneHotspot.setFocusPainted(false);
            bottoneHotspot.setOpaque(false);
            bottoneHotspot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            bottoneHotspot.addActionListener(e -> gameManager.getInterazioneObserver().tentaInterazione(h.idInterazione));

            gestore.registra(bottoneHotspot, h.centroX, h.centroY, h.larghezza, h.altezza);
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
            dialogBox.svuota();
            dialogoCorrente = null;
            battuteCorrenti = List.of();
            indiceBattuta = 0;

            dialogBox.setVisible(false);

            dialogBox.revalidate();
            dialogBox.repaint();

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
    }
}