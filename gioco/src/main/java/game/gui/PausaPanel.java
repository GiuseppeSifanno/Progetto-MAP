package game.gui;

import game.manager.GameManager;
import game.rest.WikiServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URI;

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
    private JButton btnWiki;

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

        btnSalva    =   creaBottone("Salva");
        btnCarica   =   creaBottone("Carica");
        btnMenu     =   creaBottone("Menu");
        btnChiudi   =   creaBottone("Riprendi");
        btnWiki     =   creaBottone("Wiki");

        panelPausa.add(titolo);
        panelPausa.add(Box.createVerticalStrut(20));
        panelPausa.add(btnSalva);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnCarica);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnWiki);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnMenu);
        panelPausa.add(Box.createVerticalStrut(12));
        panelPausa.add(btnChiudi);

        add(Box.createHorizontalGlue());
        add(panelPausa);
        add(Box.createHorizontalGlue());

        // GESTIONE INPUT + EVENTI
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "chiudiPausa");
        actionMap.put("chiudiPausa", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                gestoreSchermate.chiudiPausa();
            }
        });

        btnMenu.addActionListener(e     -> {
            gestoreSchermate.mostra(GestoreSchermate.MENU);
            gestoreSchermate.chiudiPausa();
        });
        btnChiudi.addActionListener(e   ->  gestoreSchermate.chiudiPausa());
        btnSalva.addActionListener(e    -> {
            SalvataggioHelper.gestisciSalvataggio(this, gameManager);
            gestoreSchermate.chiudiPausa();
        });
        btnCarica.addActionListener(e   -> {
            SalvataggioHelper.gestisciCarica(this, gameManager, gestoreSchermate);
            gestoreSchermate.chiudiPausa();
        });

        btnWiki.addActionListener(e -> {
            openWebpage(URI.create(WikiServer.URL));
        });
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

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void init() {}

    @Override
    public void aggiorna() {}

    @Override
    public void reset() {}
}