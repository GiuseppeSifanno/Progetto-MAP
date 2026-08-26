package game.gui;

import engine.manager.BaseGameManager;
import engine.model.BaseDialogo;
import engine.model.Battuta;
import game.manager.GameManager;
import game.model.Dialogo;
import game.model.Scelta;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Schermata di gioco: mostra lo sfondo della zona corrente, il testo del
 * dialogo, le scelte disponibili (bottoni dinamici) e gli hotspot cliccabili
 * per interagire con la zona.
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

    private PannelloSfondo sfondo;
    private JTextArea areaDialogo;
    private JPanel pannelloScelte;
    private JLabel etichettaMessaggio;
    private GestoreComponenti gestore;

    private final List<JButton> hotspotAttivi = new ArrayList<>();
    private Timer timerMessaggio;

    public GamePanel(BaseGameManager gameManager) {
        super(gameManager);
        setLayout(new BorderLayout());
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        sfondo = new PannelloSfondo();
        add(sfondo, BorderLayout.CENTER);

        gestore = new GestoreComponenti(sfondo);

        areaDialogo = new JTextArea();
        areaDialogo.setEditable(false);
        areaDialogo.setLineWrap(true);
        areaDialogo.setWrapStyleWord(true);
        areaDialogo.setOpaque(true);
        areaDialogo.setBackground(new Color(0, 0, 0, 180));
        areaDialogo.setForeground(Color.WHITE);
        areaDialogo.setFont(areaDialogo.getFont().deriveFont(16f));
        sfondo.add(areaDialogo);

        pannelloScelte = new JPanel();
        pannelloScelte.setOpaque(false);
        pannelloScelte.setLayout(new BoxLayout(pannelloScelte, BoxLayout.Y_AXIS));
        sfondo.add(pannelloScelte);

        etichettaMessaggio = new JLabel("", SwingConstants.CENTER);
        etichettaMessaggio.setOpaque(true);
        etichettaMessaggio.setBackground(new Color(0, 0, 0, 200));
        etichettaMessaggio.setForeground(Color.YELLOW);
        etichettaMessaggio.setVisible(false);
        sfondo.add(etichettaMessaggio);

        gestore.registra(areaDialogo, 836, 830, 1500, 180);
        gestore.registra(pannelloScelte, 836, 650, 1500, 150);
        gestore.registra(etichettaMessaggio, 836, 470, 900, 60);
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

            //cast necessario per accedere ai metodi del game manager concreto
            bottoneHotspot.addActionListener(e -> ((GameManager) gameManager).getInterazioneObserver().tentaInterazione(h.idInterazione));

            gestore.registra(bottoneHotspot, h.centroX, h.centroY, h.larghezza, h.altezza);
            hotspotAttivi.add(bottoneHotspot);
        }
    }

    // ==================== Dialogo / scelte ====================

    /** Aggiorna testo del dialogo e bottoni delle scelte in base al dialogo corrente. */
    public void aggiornaDialogo(BaseDialogo dialogo) {
        if (dialogo == null) return;

        StringBuilder testo = new StringBuilder();
        List<Battuta> battute = dialogo.getBattute();
        if (battute != null) {
            for (Battuta battuta : battute) {
                testo.append(battuta.testo()).append("\n\n");
            }
        }
        areaDialogo.setText(testo.toString().trim());

        if (dialogo instanceof Dialogo dialogoConcreto) {
            aggiornaScelte(dialogoConcreto.getScelte());
        } else {
            aggiornaScelte(List.of());
        }
    }

    /** Ricrea i bottoni delle scelte in base alla lista passata. */
    public void aggiornaScelte(List<Scelta> scelte) {
        pannelloScelte.removeAll();

        for (int i = 0; i < scelte.size(); i++) {
            Scelta scelta = scelte.get(i);
            int indice = i; // effettivamente final per la lambda

            JButton bottoneScelta = new JButton(scelta.getTesto());
            bottoneScelta.addActionListener(e ->
                    gameManager.getDialogManager().scegliOpzione(indice));

            pannelloScelte.add(bottoneScelta);
        }

        pannelloScelte.revalidate();
        pannelloScelte.repaint();
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
        String idAtto = ((GameManager) gameManager).getGameState().getIdAttoCorrente();
        if (idAtto != null) {
            aggiornaImmagine(idAtto);
        }

        BaseDialogo dialogoCorrente = gameManager.getDialogManager().getDialogo();
        if (dialogoCorrente != null) {
            aggiornaDialogo(dialogoCorrente);
        }
    }

    @Override
    public void reset() {
        areaDialogo.setText("");
        pannelloScelte.removeAll();
        pannelloScelte.revalidate();
        pannelloScelte.repaint();
        rimuoviHotspotAttuali();
        etichettaMessaggio.setVisible(false);
    }
}
