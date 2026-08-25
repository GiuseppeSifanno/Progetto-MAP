package game.gui;

import engine.model.BaseOggetto;
import game.manager.GameManager;
import game.model.oggetti.Materiale;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Giuseppe
 */
public class InventarioPanel extends BasePanel {

    private JPanel grigliaOggetti;
    private JScrollPane scrollGriglia;
    private JTextArea dettaglioOggetto;
    private JButton btnUsa;
    private JButton btnCombina;
    private JButton btnChiudi;

    private BaseOggetto oggettoSelezionato;

    public InventarioPanel(GameManager gameManager) {
        super(gameManager);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // padding esterno di tutto il pannello

        // ===== griglia oggetti (sinistra/centro) =====
        grigliaOggetti = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        grigliaOggetti.setBorder(BorderFactory.createEtchedBorder());
        scrollGriglia = new JScrollPane(grigliaOggetti);
        scrollGriglia.setPreferredSize(new Dimension(400, 300));
        add(scrollGriglia, BorderLayout.CENTER);

        // ===== pannello destro: dettaglio + bottoni =====
        JPanel pannelloDestro = new JPanel(new BorderLayout(5, 5));
        dettaglioOggetto = new JTextArea(8, 20);
        dettaglioOggetto.setBorder(BorderFactory.createEtchedBorder());
        dettaglioOggetto.setEditable(false);
        dettaglioOggetto.setLineWrap(true);
        dettaglioOggetto.setWrapStyleWord(true);
        JScrollPane scrollDettaglio = new JScrollPane(dettaglioOggetto);
        pannelloDestro.add(scrollDettaglio, BorderLayout.CENTER);

        JPanel pannelloBottoni = new JPanel(new GridLayout(3, 1, 5, 5));
        btnUsa = new JButton("Usa");
        btnCombina = new JButton("Combina");
        btnChiudi = new JButton("Chiudi");
        pannelloBottoni.add(btnUsa);
        pannelloBottoni.add(btnCombina);
        pannelloBottoni.add(btnChiudi);

        pannelloDestro.add(pannelloBottoni, BorderLayout.SOUTH);

        add(pannelloDestro, BorderLayout.EAST);

        // per ora i bottoni non fanno nulla: azioni vere quando colleghi GameUIListener
        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);

        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "chiudiInventario");
        actionMap.put("chiudiInventario", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnChiudi.doClick();
            }
        });
    }

    /**
     * Ricostruisce la griglia con la lista di oggetti passata.
     * Verrà richiamato dal listener quando arrivano eventi OGGETTO_AGGIUNTO/OGGETTO_RIMOSSO.
     */
    public void aggiornaOggetti(List<BaseOggetto> oggetti) {
        grigliaOggetti.removeAll();

        for (BaseOggetto oggetto : oggetti) {
            JButton bottoneOggetto = creaBottoneOggetto(oggetto);
            grigliaOggetti.add(bottoneOggetto);
        }

        grigliaOggetti.revalidate();
        grigliaOggetti.repaint();
    }

    private JButton creaBottoneOggetto(BaseOggetto oggetto) {
        JButton bottone = new JButton();
        bottone.setPreferredSize(new Dimension(90, 90));
        bottone.setText(oggetto.getNome());
        bottone.setVerticalTextPosition(SwingConstants.BOTTOM);
        bottone.setHorizontalTextPosition(SwingConstants.CENTER);

        ImageIcon icona = caricaIcona(oggetto.getFilename());
        if (icona != null) {
            bottone.setIcon(icona);
            bottone.setText(""); // solo icona se disponibile; togli questa riga se vuoi tenere anche il nome sotto
        }

        bottone.setToolTipText(oggetto.getNome());
        bottone.addActionListener(e -> selezionaOggetto(oggetto));
        return bottone;
    }

    /**
     * Carica un'icona dagli assets, ridimensionata per il bottone.
     * Ritorna null se il filename è assente o l'immagine non viene trovata
     * (es. i flag tecnici in data.sql hanno image_name = NULL).
     */
    private ImageIcon caricaIcona(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        java.net.URL risorsa = getClass().getClassLoader().getResource("assets/" + filename);
        if (risorsa == null) {
            return null;
        }

        ImageIcon originale = new ImageIcon(risorsa);
        Image ridimensionata = originale.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        return new ImageIcon(ridimensionata);
    }

    private void selezionaOggetto(BaseOggetto oggetto) {
        this.oggettoSelezionato = oggetto;

        StringBuilder testo = new StringBuilder();
        testo.append(oggetto.getNome()).append("\n\n");
        testo.append(oggetto.getDescrizione()).append("\n");

        if (oggetto instanceof Materiale materiale) {
            testo.append("\nQuantità: ").append(materiale.getQuantita());
        }

        dettaglioOggetto.setText(testo.toString());
        btnUsa.setEnabled(true);
        btnCombina.setEnabled(true);
    }

    @Override
    public void init() {
        aggiorna();
    }

    @Override
    public void aggiorna() {
        aggiornaOggetti(gameManager.getGameState().getInventario().oggetti());
    }

    public void reset() {
        grigliaOggetti.removeAll();
        dettaglioOggetto.setText("");
        oggettoSelezionato = null;
        btnUsa.setEnabled(false);
        btnCombina.setEnabled(false);
        grigliaOggetti.revalidate();
        grigliaOggetti.repaint();
    }

    // getter utili per quando collegherai gli eventi al GameManager
    public JButton getBtnUsa() { return btnUsa; }
    public JButton getBtnCombina() { return btnCombina; }
    public JButton getBtnChiudi() { return btnChiudi; }
    public BaseOggetto getOggettoSelezionato() { return oggettoSelezionato; }
}