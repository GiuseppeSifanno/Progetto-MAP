package game.gui;

import game.manager.GameManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.GapContent;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PausaPanel extends BasePanel {

    // ===== palette =====
    private static final Color COLORE_SFONDO_PANNELLO = new Color(45, 33, 27);      // legno scuro
    private static final Color COLORE_BORDO           = new Color(198, 156, 109);   // legno chiaro/sabbia
    private static final Color COLORE_BOTTONE         = new Color(198, 156, 109);   // sabbia
    private static final Color COLORE_BOTTONE_HOVER   = new Color(216, 178, 132);
    private static final Color COLORE_TESTO_BOTTONE   = new Color(45, 33, 27);
    private static final Color COLORE_TITOLO          = new Color(240, 220, 190);

    private JPanel panelPausa;
    private JButton btnSalva;
    private JButton btnCarica;
    private JButton btnMenu;
    private JButton btnChiudi;

    private final GestoreSchermate gestoreSchermate;

    public PausaPanel(GestoreSchermate gestoreSchermate, GameManager gameManager) {
        super(gameManager);
        this.gestoreSchermate = gestoreSchermate;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setOpaque(false); // lascia vedere lo sfondo scurito sotto

        panelPausa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO_PANNELLO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelPausa.setOpaque(false); // fondamentale, altrimenti Swing riempie comunque il rettangolo esterno
        panelPausa.setLayout(new BoxLayout(panelPausa, BoxLayout.Y_AXIS));
        panelPausa.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(20, COLORE_BORDO),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        panelPausa.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel titolo = new JLabel("PAUSA (?)");
        titolo.setToolTipText("PREMI ESC PER USCIRE");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titolo.setForeground(COLORE_TITOLO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSalva = creaBottone("Salva");
        btnCarica = creaBottone("Carica");
        btnMenu = creaBottone("Menu");
        btnChiudi = creaBottone("Riprendi");

        panelPausa.add(titolo);
        panelPausa.add(Box.createVerticalStrut(20));
        panelPausa.add(btnSalva);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnCarica);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnMenu);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnChiudi);

        add(Box.createHorizontalGlue());
        add(panelPausa);
        add(Box.createHorizontalGlue());

        // GESTIONE INPUT + EVENTI

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "chiudiPausa");
        actionMap.put("chiudiPausa", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnChiudi.doClick();
            }
        });

        btnMenu.addActionListener(e     ->  gestoreSchermate.mostra(GestoreSchermate.MENU));
        btnChiudi.addActionListener(e   ->  gestoreSchermate.chiudiPausa());
        btnSalva.addActionListener(e    ->  gestisciSalvataggio());
        btnCarica.addActionListener(e   ->  gestisciCarica());
    }

    private void gestisciSalvataggio() {
        List<Integer> lista = new ArrayList<>(gameManager.getSaveManager().listaSalvataggi());
        int slot = chiediSlot(lista, "Salva");
        if (slot == -1) return;
        try {
            gameManager.salvaPartita(slot);
            JOptionPane.showMessageDialog(this, "Salvataggio completato nello slot: " + slot, "Salvataggio", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Errore durante il salvataggio: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gestisciCarica() {
        List<Integer> lista = new ArrayList<>(gameManager.getSaveManager().listaSalvataggi());
        int slot = chiediSlot(lista, "Carica");
        if (slot == -1) return;
        try {
            gameManager.caricaPartita(slot);
            JOptionPane.showMessageDialog(this, "Caricamento completato dello slot: " + slot, "Caricamento", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Errore durante il caricamento: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int chiediSlot(List<Integer> lista, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);

        JPanel contenuto = new JPanel();
        contenuto.setLayout(new BoxLayout(contenuto, BoxLayout.Y_AXIS));
        contenuto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (!lista.isEmpty()) {
            JLabel etichettaSlotEsistenti = new JLabel("Slot esistenti: " + lista.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(""));
            etichettaSlotEsistenti.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenuto.add(etichettaSlotEsistenti);
            contenuto.add(Box.createVerticalStrut(15));
        }

        JLabel etichettaInput = new JLabel("Inserisci il numero di slot:");
        etichettaInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenuto.add(etichettaInput);
        contenuto.add(Box.createVerticalStrut(10));

        //FORMATTER per il campo di input
        NumberFormatter formatter = getNumberFormatter();

        JFormattedTextField inputSlot = new JFormattedTextField(formatter);
        inputSlot.setColumns(5);

        inputSlot.setHorizontalAlignment(JFormattedTextField.CENTER);
        inputSlot.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputSlot.setMaximumSize(new Dimension(100, 28));
        contenuto.add(inputSlot);
        contenuto.add(Box.createVerticalStrut(20));

        // bottoni conferma/annulla
        JPanel pannelloBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnConferma = new JButton("Conferma");
        JButton btnAnnulla = new JButton("Annulla");
        pannelloBottoni.add(btnConferma);
        pannelloBottoni.add(btnAnnulla);
        contenuto.add(pannelloBottoni);

        final int[] risultato = {-1};

        btnConferma.addActionListener(e -> {
            String testo = inputSlot.getText();
            if (!testo.isBlank()) {
                risultato[0] = Integer.parseInt(testo);
            }
            dialog.dispose();
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        dialog.add(contenuto);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocca qui finché non si chiude

        return risultato[0];
    }

    private static NumberFormatter getNumberFormatter() {
        NumberFormat formato = NumberFormat.getIntegerInstance();
        formato.setGroupingUsed(false);

        NumberFormatter formatter = new NumberFormatter(formato) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.isEmpty()) {
                    return null; // permette il campo vuoto senza lanciare eccezione
                }
                return super.stringToValue(text);
            }
        };
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setAllowsInvalid(false); // ora permette stati intermedi (incluso vuoto)
        return formatter;
    }

    private JButton creaBottone(String testo) {
        JButton bottone = new JButton(testo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? COLORE_BOTTONE_HOVER : COLORE_BOTTONE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottone.setFont(new Font("SansSerif", Font.BOLD, 15));
        bottone.setForeground(COLORE_TESTO_BOTTONE);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setBorderPainted(false);
        bottone.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottone.setMaximumSize(new Dimension(180, 42));
        bottone.setPreferredSize(new Dimension(180, 42));
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return bottone;
    }

    public JButton getBtnSalva() { return btnSalva; }
    public JButton getBtnCarica() { return btnCarica; }
    public JButton getBtnMenu() { return btnMenu; }
    public JButton getBtnChiudi() { return btnChiudi; }

    @Override
    public void init() {}

    @Override
    public void aggiorna() {}

    @Override
    public void reset() {}
}