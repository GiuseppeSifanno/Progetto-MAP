package game.gui;

import engine.model.BaseOggetto;
import game.manager.GameManager;
import game.model.oggetti.Materiale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioPanel extends BasePanel {
    // ============================================================
    // PALETTE CONDIVISA CON PausaPanel
    // ============================================================

    private static final Color COLORE_SFONDO = new Color(45, 33, 27);
    private static final Color COLORE_BORDO = new Color(198, 156, 109);
    private static final Color COLORE_BOTTONE = new Color(198, 156, 109);
    private static final Color COLORE_BOTTONE_HOVER = new Color(216, 178, 132);
    private static final Color COLORE_TESTO_BOTTONE = new Color(45, 33, 27);
    private static final Color COLORE_TESTO = new Color(240, 220, 190);

    // Colori specifici per le schede degli oggetti
    private static final Color COLORE_RIQUADRO = new Color(58, 43, 35);
    private static final Color COLORE_RIQUADRO_HOVER = new Color(76, 57, 45);
    private static final Color COLORE_SELEZIONATO = new Color(216, 178, 132);

    // ============================================================
    // COMPONENTI
    // ============================================================

    private JPanel grigliaOggetti;
    private JScrollPane scrollGriglia;

    private JTextArea dettaglioOggetto;

    private JButton btnUsa;
    private JButton btnCombina;
    private JButton btnChiudi;

    private BaseOggetto oggettoSelezionato;
    private final List<BaseOggetto> selezionati = new ArrayList<>();

    // campo statico per il caching delle immagini
    private static final Map<String, ImageIcon> CACHE_ICONE = new HashMap<>();

    // ============================================================
    // COSTRUTTORE
    // ============================================================

    public InventarioPanel(GameManager gameManager) {
        super(gameManager);
        initComponents();
    }

    // ============================================================
    // INIZIALIZZAZIONE GUI
    // ============================================================

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ========================================================
        // PANNELLO PRINCIPALE
        // ========================================================

        JPanel pannelloPrincipale = createPannelloPrincipale();

        // ========================================================
        // TITOLO
        // ========================================================

        JLabel titolo = new JLabel("INVENTARIO");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titolo.setForeground(COLORE_TESTO);
        titolo.setHorizontalAlignment(SwingConstants.CENTER);
        titolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        pannelloPrincipale.add(titolo, BorderLayout.NORTH);

        // ========================================================
        // GRIGLIA OGGETTI
        // ========================================================
        grigliaOggetti = new JPanel(
                new GridLayout(
                        0,    // numero di righe automatico
                        3,          // esattamente 3 colonne
                        8,         // spazio orizzontale
                        8          // spazio verticale
                )
        );

        grigliaOggetti.setOpaque(false);
        grigliaOggetti.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 5, 5, 5
                )
        );

        scrollGriglia = new JScrollPane(grigliaOggetti);
        //scrollGriglia.setPreferredSize(new Dimension(430, 320));
        scrollGriglia.setOpaque(false);
        scrollGriglia.getViewport().setOpaque(false);
        scrollGriglia.setVerticalScrollBar(new JScrollBar());
        scrollGriglia.getVerticalScrollBar().setUnitIncrement(16);
        scrollGriglia.setBorder(new BordoArrotondato(14, COLORE_BORDO));

        pannelloPrincipale.add(scrollGriglia, BorderLayout.CENTER);

        // ========================================================
        // PANNELLO DESTRO
        // ========================================================

        JPanel pannelloDestro = new JPanel(new BorderLayout(10, 10));
        pannelloDestro.setOpaque(false);
        pannelloDestro.setPreferredSize(new Dimension(250, 320));

        // ========================================================
        // TITOLO DETTAGLI
        // ========================================================

        JLabel titoloDettagli = new JLabel("DETTAGLI");
        titoloDettagli.setFont(new Font("SansSerif", Font.BOLD, 17));
        titoloDettagli.setForeground(COLORE_TESTO);
        titoloDettagli.setHorizontalAlignment(SwingConstants.CENTER);

        pannelloDestro.add(titoloDettagli, BorderLayout.NORTH);

        // ========================================================
        // DETTAGLIO OGGETTO
        // ========================================================

        dettaglioOggetto = new JTextArea(8, 20);
        dettaglioOggetto.setEditable(false);
        dettaglioOggetto.setLineWrap(true);
        dettaglioOggetto.setWrapStyleWord(true);
        dettaglioOggetto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dettaglioOggetto.setForeground(COLORE_TESTO);
        dettaglioOggetto.setBackground(COLORE_RIQUADRO);
        dettaglioOggetto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollDettaglio = new JScrollPane(dettaglioOggetto);
        scrollDettaglio.setOpaque(false);
        scrollDettaglio.getViewport().setOpaque(false);
        scrollDettaglio.setBorder(new BordoArrotondato(14, COLORE_BORDO));

        pannelloDestro.add(scrollDettaglio, BorderLayout.CENTER);

        // ========================================================
        // BOTTONI
        // ========================================================

        JPanel pannelloBottoni = new JPanel(new GridLayout(3, 1, 8, 8));
        pannelloBottoni.setOpaque(false);

        btnUsa = creaBottone("Usa");
        btnCombina = creaBottone("Combina");
        btnChiudi = creaBottone("Chiudi");

        btnCombina.addActionListener(e -> eseguiCombina());

        pannelloBottoni.add(btnUsa);
        pannelloBottoni.add(btnCombina);
        pannelloBottoni.add(btnChiudi);

        pannelloDestro.add(pannelloBottoni, BorderLayout.SOUTH);
        pannelloPrincipale.add(pannelloDestro, BorderLayout.EAST);

        add(pannelloPrincipale, BorderLayout.CENTER);

        // ========================================================
        // STATO INIZIALE
        // ========================================================

        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);

        // ========================================================
        // TASTO ESC
        // ========================================================

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "chiudiInventario");
        actionMap.put("chiudiInventario", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnChiudi.doClick();
            }
        });
    }

    private JPanel createPannelloPrincipale() {
        JPanel pannelloPrincipale = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        pannelloPrincipale.setOpaque(false);
        pannelloPrincipale.setBorder(BorderFactory.createCompoundBorder(new BordoArrotondato(20, COLORE_BORDO), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return pannelloPrincipale;
    }

    // ============================================================
    // GRIGLIA INVENTARIO
    // ============================================================

    public void aggiornaOggetti(List<BaseOggetto> oggetti) {
        grigliaOggetti.removeAll();

        // Evita riferimenti stantii: gli oggetti vengono ricreati ad ogni
        // refresh, quindi eventuali istanze già selezionate non
        // corrisponderebbero più a nulla nella nuova griglia.
        selezionati.clear();
        oggettoSelezionato = null;
        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);
        dettaglioOggetto.setText("");

        for (BaseOggetto oggetto : oggetti) {
            JButton bottone = creaBottoneOggetto(oggetto);
            grigliaOggetti.add(bottone);
        }
        grigliaOggetti.revalidate();
        grigliaOggetti.repaint();
    }

    // ============================================================
    // BOTTONE OGGETTO
    // ============================================================

    private JButton creaBottoneOggetto(BaseOggetto oggetto) {
        JButton bottone = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color sfondo;
                if (selezionati.contains(oggetto)) {
                    sfondo = COLORE_SELEZIONATO;
                } else if (getModel().isRollover()) {
                    sfondo = COLORE_RIQUADRO_HOVER;
                } else {
                    sfondo = COLORE_RIQUADRO;
                }

                g2.setColor(sfondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        bottone.setPreferredSize(new Dimension(80, 80));
        bottone.setFont(new Font("SansSerif", Font.BOLD, 12));
        bottone.setForeground(COLORE_TESTO);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setBorder(new BordoArrotondato(14, COLORE_BORDO));
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ========================================================
        // ICONA
        // ========================================================

        ImageIcon icona = caricaIcona(oggetto.getFilename());
        if (icona != null) {
            bottone.setIcon(icona);
            bottone.setText(oggetto.getNome());
            bottone.setVerticalTextPosition(SwingConstants.BOTTOM);
            bottone.setHorizontalTextPosition(SwingConstants.CENTER);
        } else {
            bottone.setText(oggetto.getNome());
        }
        bottone.setToolTipText(oggetto.getNome());
        bottone.addActionListener(e -> selezionaOggetto(oggetto));
        return bottone;
    }

    // ============================================================
    // CARICAMENTO ICONE
    // ============================================================

    private ImageIcon caricaIcona(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        return CACHE_ICONE.computeIfAbsent(filename, f -> {
            java.net.URL risorsa = getClass().getClassLoader().getResource("assets/" + f);
            if (risorsa == null) return null;

            ImageIcon originale = new ImageIcon(risorsa);
            Image ridimensionata = originale.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            return new ImageIcon(ridimensionata);
        });
    }

    // ============================================================
    // SELEZIONE OGGETTO
    // ============================================================

    private void selezionaOggetto(BaseOggetto oggetto) {
        if (selezionati.contains(oggetto)) {
            selezionati.remove(oggetto);
        } else {
            selezionati.add(oggetto);
        }
        // Per "Usa" e per il testo dei dettagli, l'oggetto di riferimento
        // resta l'ultimo toccato (o null se la selezione è stata svuotata).
        this.oggettoSelezionato = selezionati.isEmpty() ? null : oggetto;

        aggiornaDettagliSelezione();

        btnUsa.setEnabled(selezionati.size() == 1);
        btnCombina.setEnabled(selezionati.size() >= 2);

        // Aggiorna l'evidenziazione degli oggetti selezionati
        grigliaOggetti.repaint();
    }

    /** Mostra i dettagli di un singolo oggetto selezionato, o un riepilogo se ce n'è più di uno. */
    private void aggiornaDettagliSelezione() {
        if (selezionati.isEmpty()) {
            dettaglioOggetto.setText("");
            return;
        }

        if (selezionati.size() == 1) {
            BaseOggetto oggetto = selezionati.get(0);
            StringBuilder testo = new StringBuilder();
            testo.append(oggetto.getNome()).append("\n\n");
            testo.append(oggetto.getDescrizione()).append("\n");
            if (oggetto instanceof Materiale materiale) {
                testo.append("\nQuantità: ").append(materiale.getQuantita());
            }
            dettaglioOggetto.setText(testo.toString());
        } else {
            StringBuilder testo = new StringBuilder();
            testo.append(selezionati.size()).append(" oggetti selezionati:\n\n");
            for (BaseOggetto o : selezionati) {
                testo.append("- ").append(o.getNome()).append("\n");
            }
            dettaglioOggetto.setText(testo.toString());
        }
    }

    /** Chiamato dal bottone "Combina": prova a combinare tutti gli oggetti attualmente selezionati. */
    private void eseguiCombina() {
        if (selezionati.size() < 2) {
            return;
        }

        List<String> ids = new ArrayList<>();
        for (BaseOggetto o : selezionati) {
            ids.add(o.getId());
        }

        BaseOggetto risultato = gameManager.combinaOggetti(ids);

        selezionati.clear();
        oggettoSelezionato = null;
        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);
        grigliaOggetti.repaint(); // altrimenti le caselle restano evidenziate anche se non più selezionate

        if (risultato != null) {
            dettaglioOggetto.setText("Hai ottenuto: " + risultato.getNome() + "\n\n" + risultato.getDescrizione());
        } else {
            dettaglioOggetto.setText("Questi oggetti non si possono combinare insieme.");
        }
        // Nota: se la combinazione riesce, gameManager.combinaOggetti() ha già
        // rimosso gli ingredienti e aggiunto il risultato all'inventario,
        // quindi aggiorna() viene già richiamato automaticamente dagli eventi
        // OGGETTO_RIMOSSO/OGGETTO_AGGIUNTO (vedi GameUIListenerImpl).
    }
    // ============================================================
    // BOTTONI PRINCIPALI
    // ============================================================
    private JButton creaBottone(String testo) {
        JButton bottone = new JButton(testo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color sfondo;
                if (!isEnabled()) {
                    sfondo = new Color(110, 91, 75);
                } else if (getModel().isRollover()) {
                    sfondo = COLORE_BOTTONE_HOVER;
                } else {
                    sfondo = COLORE_BOTTONE;
                }
                g2.setColor(sfondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottone.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottone.setForeground(COLORE_TESTO_BOTTONE);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setBorderPainted(false);
        bottone.setPreferredSize(new Dimension(180, 42));
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return bottone;
    }

    // ============================================================
    // CICLO DI VITA
    // ============================================================

    @Override
    public void init() {
        aggiorna();
    }


    @Override
    public void aggiorna() {
        aggiornaOggetti(gameManager.getGameState().getInventario().oggetti());
    }


    @Override
    public void reset() {
        grigliaOggetti.removeAll();
        dettaglioOggetto.setText("");
        oggettoSelezionato = null;
        selezionati.clear();

        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);

        grigliaOggetti.revalidate();
        grigliaOggetti.repaint();
    }

    // ============================================================
    // GETTER
    // ============================================================

    public JButton getBtnUsa() {
        return btnUsa;
    }

    public JButton getBtnCombina() {
        return btnCombina;
    }

    public JButton getBtnChiudi() {
        return btnChiudi;
    }

    public BaseOggetto getOggettoSelezionato() {
        return oggettoSelezionato;
    }
}