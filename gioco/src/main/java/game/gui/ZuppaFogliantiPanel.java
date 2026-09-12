package game.gui;

import engine.GUI.BasePanel;
import game.manager.GameManager;
import game.minigioco.ZuppaFogliantiManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pannello dedicato al minigioco della Zuppa dei Foglianti.
 * Il banner obiettivo e il popup "erba trovata" NON sono più di proprietà
 * di questo pannello: passano dal canale condiviso di GamePanel, per
 * evitare che due banner o due popup finiscano nello stesso punto schermo.
 */
public class ZuppaFogliantiPanel extends BasePanel {

    private record ErbaHotspot(
            String idErba, String nome, String assetIcona,
            int centroX, int centroY, int larghezza, int altezza
    ) {}

    private static final String ASSET_ERBA_DEFAULT = "/assets/Erba.png";
    private static final String ASSET_RADICI = "/assets/Radici.png";
    private static final String ASSET_ZUPPA = "/assets/zuppa.png";
    private static final String ASSET_RACCOLI_ERBE = "/assets/RaccogliErbe.png";
    private static final String ASSET_SENTIERO = "/assets/Sentiero.png";

    private static final int ERBE_CORRETTE_RICHIESTE = 4;
    private static final int DELAY_TIMER_CHIUSURA_MS = 3000;

    private static final Color COLORE_SFONDO_OVERLAY = new Color(20, 15, 10, 235);
    private static final Color COLORE_SFONDO_CONTATORE = new Color(20, 15, 10, 220);
    private static final Color COLORE_SFONDO_TRANSIZIONE = new Color(10, 10, 10, 200);
    private static final Color COLORE_BORDO_DORATO = new Color(198, 156, 109);
    private static final Color COLORE_TESTO_CHIARO = new Color(240, 220, 190);
    private static final Color COLORE_TESTO_SENTIERO = new Color(232, 226, 214);
    private static final Color COLORE_DEBUG_HOTSPOT = new Color(0, 200, 0, 90);
    private static final Color COLORE_DEBUG_BORDO = new Color(0, 200, 0);

    private static final List<ErbaHotspot> ERBE_RACCOGLIBILI = List.of(
            new ErbaHotspot("o20", "Fiori Gialli", ASSET_ERBA_DEFAULT, 670, 500, 110, 110),
            new ErbaHotspot("o21", "Fiori Viola", ASSET_ERBA_DEFAULT, 870, 745, 110, 110),
            new ErbaHotspot("o22", "Fiori Azzurri", ASSET_ERBA_DEFAULT, 1075, 640, 110, 110),
            new ErbaHotspot("o23", "Bacche Rosse", ASSET_ERBA_DEFAULT, 985, 705, 110, 110),
            new ErbaHotspot("o24", "Funghi Chiazzati", ASSET_ERBA_DEFAULT, 205, 665, 130, 110),
            new ErbaHotspot("o25", "Radice Contorta", ASSET_RADICI, 560, 615, 120, 100),
            new ErbaHotspot("o26", "Radice Nodosa", ASSET_RADICI, 1290, 560, 120, 100)
    );

    private final GamePanel gamePanel;
    private final PannelloSfondo sfondo;
    private final GestoreComponenti gestore;
    private final Runnable onAvvio;

    private final Map<String, JButton> hotspotErbePerId = new HashMap<>();

    private JLabel contatoreErbe;
    private JPanel overlayZuppaCompletata;
    private JPanel overlayTransizioneSentiero;
    private JPanel overlayAvvia;

    private int erbeCorretteRaccolte = 0;

    public ZuppaFogliantiPanel(
            GameManager gameManager,
            GamePanel gamePanel,
            PannelloSfondo sfondo,
            GestoreComponenti gestore,
            Runnable onAvvio
    ) {
        super(gameManager);
        this.gamePanel = gamePanel;
        this.sfondo = sfondo;
        this.gestore = gestore;
        this.onAvvio = onAvvio;

        setOpaque(false);
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        contatoreErbe = new JLabel("", SwingConstants.CENTER);
        contatoreErbe.setOpaque(true);
        contatoreErbe.setBackground(COLORE_SFONDO_CONTATORE);
        contatoreErbe.setForeground(COLORE_TESTO_CHIARO);
        contatoreErbe.setFont(contatoreErbe.getFont().deriveFont(Font.BOLD, 20f));
        contatoreErbe.setBorder(new BordoArrotondato(14, COLORE_BORDO_DORATO));
        contatoreErbe.setVisible(false);
        sfondo.add(contatoreErbe);
        gestore.registra(contatoreErbe, 1400, 70, 240, 60);

        overlayZuppaCompletata = creaOverlayZuppaCompletata();
        sfondo.add(overlayZuppaCompletata);
        gestore.registraCentrato(overlayZuppaCompletata, 420, 460);
        overlayZuppaCompletata.setVisible(false);

        overlayTransizioneSentiero = creaOverlayTransizioneSentiero();
        sfondo.add(overlayTransizioneSentiero);
        gestore.registraCentrato(overlayTransizioneSentiero, 900, 320);
        overlayTransizioneSentiero.setVisible(false);

        overlayAvvia = creaOverlayAvvia();
        sfondo.add(overlayAvvia);
        gestore.registraCentrato(overlayAvvia, 520, 220);
        overlayAvvia.setVisible(false);
    }

    static void setStyle(JLabel label, Color coloreSfondo) {
        label.setOpaque(true);
        label.setBackground(coloreSfondo);
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(17f));
        label.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(20, new Color(255, 255, 255, 60)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    private JPanel creaOverlayZuppaCompletata() {
        JPanel pannello = creaOverlayBase(24, 16, 16, 12);

        JLabel immagine = new JLabel(
                GamePanel.caricaIconaAsset(ASSET_ZUPPA, 100, 100), SwingConstants.CENTER
        );
        pannello.add(immagine, BorderLayout.CENTER);

        JLabel messaggio = new JLabel("La zuppa è pronta!", SwingConstants.CENTER);
        messaggio.setForeground(COLORE_TESTO_CHIARO);
        messaggio.setFont(messaggio.getFont().deriveFont(Font.BOLD, 18f));
        pannello.add(messaggio, BorderLayout.SOUTH);

        return pannello;
    }

    private JPanel creaOverlayBase(int arco, int top, int laterale, int bottom) {
        JPanel pannello = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO_OVERLAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(arco, COLORE_BORDO_DORATO),
                BorderFactory.createEmptyBorder(top, laterale, bottom, laterale)
        ));
        return pannello;
    }

    private JPanel creaOverlayTransizioneSentiero() {
        JPanel pannello = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO_TRANSIZIONE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pannello.setOpaque(false);
        pannello.setBorder(BorderFactory.createEmptyBorder(25, 40, 28, 40));
        pannello.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel testo = new JLabel(
                "<html><div style='text-align:center;'>Clicca per proseguire.</div></html>",
                SwingConstants.CENTER
        );
        testo.setFont(new Font(Font.SERIF, Font.ITALIC, 22));
        testo.setForeground(COLORE_TESTO_SENTIERO);
        pannello.add(testo, BorderLayout.CENTER);

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

    private JPanel creaOverlayAvvia() {
        JPanel pannello = creaOverlayBase(24, 20, 30, 20);

        JButton btnInizia = new JButton("INIZIA MINIGIOCO");
        setStyle(btnInizia, COLORE_TESTO_CHIARO);
        btnInizia.addActionListener(e -> {
            overlayAvvia.setVisible(false);
            onAvvio.run();
            gameManager.avviaMinigiocoZuppa();
        });

        pannello.add(btnInizia, BorderLayout.CENTER);
        return pannello;
    }

    static void setStyle(JButton bottone, Color coloreTesto) {
        bottone.setFont(GamePanel.caricaFontAntico(30f));
        bottone.setForeground(coloreTesto);
        bottone.setContentAreaFilled(false);
        bottone.setBorderPainted(false);
        bottone.setFocusPainted(false);
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottone.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void mostraBottoneIniziaMinigioco() {
        mostraOverlayInPrimoPiano(overlayAvvia);
    }

    public void avviaFaseRaccoltaErbe() {
        onAvvio.run();
        erbeCorretteRaccolte = 0;
        aggiornaContatoreErbe();

        sfondo.setImmagineSfondo(ASSET_RACCOLI_ERBE);
        rimuoviHotspotErbe();

        boolean debugVisibile = true;
        for (ErbaHotspot eh : ERBE_RACCOGLIBILI) {
            JButton bottone = creaBottoneHotspot(eh, debugVisibile);
            gestore.registra(bottone, eh.centroX(), eh.centroY(), eh.larghezza(), eh.altezza());
            hotspotErbePerId.put(eh.idErba(), bottone);
        }

        contatoreErbe.setVisible(true);
        gamePanel.nascondiBanner();
        gamePanel.mostraBanner(
                "<html><div style='text-align:center;'>Trova le erbe e le radici commestibili per la zuppa: alcune sono velenose!<br>"
                        + "Raccoglile finché non ne hai " + ERBE_CORRETTE_RICHIESTE + " buone.</div></html>"
        );

        revalidate();
        repaint();
    }

    private JButton creaBottoneHotspot(ErbaHotspot eh, boolean debugVisibile) {
        JButton bottone = new JButton();
        bottone.setFocusPainted(false);
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottone.addActionListener(e -> gameManager.selezionaErba(eh.idErba()));

        if (debugVisibile) {
            bottone.setContentAreaFilled(true);
            bottone.setBorderPainted(true);
            bottone.setOpaque(true);
            bottone.setBackground(COLORE_DEBUG_HOTSPOT);
            bottone.setBorder(BorderFactory.createLineBorder(COLORE_DEBUG_BORDO, 2));
            bottone.setForeground(Color.WHITE);
            bottone.setFont(bottone.getFont().deriveFont(Font.BOLD, 11f));
            bottone.setText(eh.nome());
        } else {
            bottone.setContentAreaFilled(false);
            bottone.setBorderPainted(false);
            bottone.setOpaque(false);
        }
        return bottone;
    }

    private void rimuoviHotspotErbe() {
        for (JButton b : hotspotErbePerId.values()) gestore.rimuovi(b);
        hotspotErbePerId.clear();
    }

    private void rimuoviHotspotErbaPerId(String idErba) {
        JButton b = hotspotErbePerId.remove(idErba);
        if (b != null) gestore.rimuovi(b);
    }

    private void aggiornaContatoreErbe() {
        contatoreErbe.setText("Erbe buone: " + erbeCorretteRaccolte + "/" + ERBE_CORRETTE_RICHIESTE);
    }

    private ErbaHotspot trovaErba(String idErba) {
        for (ErbaHotspot eh : ERBE_RACCOGLIBILI) {
            if (eh.idErba().equals(idErba)) return eh;
        }
        return null;
    }

    /** Il popup "erba trovata" ora passa dal canale condiviso di GamePanel:
     *  nessun overlay proprio, quindi nessun rischio di sovrapposizione con
     *  altri popup (es. "oggetto trovato" della miniera). */
    public void mostraEsitoErba(ZuppaFogliantiManager.EsitoErba esito) {
        ErbaHotspot trovata = trovaErba(esito.idErba());
        String nome = trovata != null ? trovata.nome() : esito.idErba();
        String asset = trovata != null ? trovata.assetIcona() : ASSET_ERBA_DEFAULT;

        if (esito.corretta()) {
            erbeCorretteRaccolte++;
            aggiornaContatoreErbe();
        }

        rimuoviHotspotErbaPerId(esito.idErba());
        // Icona più piccola: 280x280 era pensato per gli oggetti della miniera,
        // troppo grande per un fiore/erba della zuppa.
        gamePanel.mostraPopupOggetto(asset, nome, 100, 100, 220, 220);

        if (erbeCorretteRaccolte >= ERBE_CORRETTE_RICHIESTE) {
            gamePanel.nascondiBanner();
            gamePanel.mostraBanner(
                    "<html><div style='text-align:center;'>Hai tutte le erbe/radici che ti servono!<br>"
                            + "Apri l'inventario (tasto E) e usa <b>Combina</b> per preparare la zuppa.</div></html>"
            );
        }
    }

    public void mostraZuppaCompletata() {
        gamePanel.nascondiBanner();
        mostraOverlayInPrimoPiano(overlayZuppaCompletata);

        Timer timerChiusura = new Timer(DELAY_TIMER_CHIUSURA_MS, e -> {
            overlayZuppaCompletata.setVisible(false);
            contatoreErbe.setVisible(false);
            gamePanel.nascondiBanner();
            rimuoviHotspotErbe();
            mostraTransizioneSentiero();
        });
        timerChiusura.setRepeats(false);
        timerChiusura.start();
    }

    private void mostraTransizioneSentiero() {
        sfondo.setImmagineSfondo(ASSET_SENTIERO);
        mostraOverlayInPrimoPiano(overlayTransizioneSentiero);
    }

    private void mostraOverlayInPrimoPiano(JPanel overlay) {
        if (overlay != null) {
            sfondo.setComponentZOrder(overlay, 0);
            overlay.setVisible(true);
            overlay.revalidate();
            overlay.repaint();
        }
    }

    @Override
    public void init() { }

    @Override
    public void aggiorna() { }

    public void reset() {
        rimuoviHotspotErbe();

        if (contatoreErbe != null) contatoreErbe.setVisible(false);
        if (overlayZuppaCompletata != null) overlayZuppaCompletata.setVisible(false);
        if (overlayTransizioneSentiero != null) overlayTransizioneSentiero.setVisible(false);
        if (overlayAvvia != null) overlayAvvia.setVisible(false);

        erbeCorretteRaccolte = 0;
    }
}