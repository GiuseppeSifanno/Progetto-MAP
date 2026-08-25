package game.gui;

import game.manager.GameManager;
import game.ui.GameUIListener;
import game.gui.InventarioPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.text.Keymap;

/**
 * @author Giuseppe
 * @author Graziana
 */
public class GestoreSchermate {
    public static final String MENU = "menu";
    public static final String PROVA1 = "prova1";

    private final GameUIListener listener;
    private final CardLayout cardLayout;
    private final JPanel contenitore;
    private final JLayeredPane layeredPane;
    private final JPanel sfondoScurito;
    private final InventarioPanel inventarioPanel;

    public GestoreSchermate(JFrame frame, GameManager gameManager) {
        layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        // ===== layer base: CardLayout con le schermate normali =====
        cardLayout = new CardLayout();
        contenitore = new JPanel(cardLayout);
        layeredPane.add(contenitore, JLayeredPane.DEFAULT_LAYER);

        addSchermata(MENU, new MenuIniziale(this, gameManager));
        addSchermata(PROVA1, new Prova1(gameManager));

        // ===== layer overlay: sfondo scurito + inventario =====
        sfondoScurito = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sfondoScurito.setOpaque(false); // fondamentale: disabilita l'ottimizzazione che ignora l'alpha
        sfondoScurito.setVisible(false);
        layeredPane.add(sfondoScurito, JLayeredPane.PALETTE_LAYER);

        inventarioPanel = new InventarioPanel(gameManager);
        inventarioPanel.setVisible(false);
        layeredPane.add(inventarioPanel, JLayeredPane.MODAL_LAYER);

        inventarioPanel.getBtnChiudi().addActionListener(e -> chiudiInventario());

        //TODO completare la classe GameUIListenerImpl
        //il listener collega gli eventi di GameManager alla GUI (menu, inventario, ecc.)
        this.listener = null; // vedi nota sotto
        gameManager.collegaGUI(listener);

        InputMap inputMap = contenitore.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        ActionMap actionMap = contenitore.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("E"), "apriInventario");
        actionMap.put("apriInventario", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriInventario();
            }
        });

        // ricalcola i bounds ogni volta che il layeredPane cambia dimensione
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                aggiornaBounds();
            }
        });
        aggiornaBounds();
    }

    private void aggiornaBounds() {
        int w = layeredPane.getWidth();
        int h = layeredPane.getHeight();

        contenitore.setBounds(0, 0, w, h);
        sfondoScurito.setBounds(0, 0, w, h);

        // inventario centrato, dimensione fissa (o proporzionale se preferisci)
        int invW = 700, invH = 500;
        inventarioPanel.setBounds((w - invW) / 2, (h - invH) / 2, invW, invH);
    }

    public void addSchermata(String nome, JPanel schermata) {
        contenitore.add(nome, schermata);
    }

    public void mostra(String nome) {
        cardLayout.show(contenitore, nome);
    }

    public void apriInventario() {
        inventarioPanel.init(); // richiama aggiorna() per popolare la griglia
        sfondoScurito.setVisible(true);
        inventarioPanel.setVisible(true);
        layeredPane.moveToFront(inventarioPanel);
    }

    public void chiudiInventario() {
        inventarioPanel.setVisible(false);
        sfondoScurito.setVisible(false);
    }

    public InventarioPanel getInventarioPanel() {
        return inventarioPanel;
    }
}