package game.gui;

import game.manager.GameManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import javax.swing.*;

/**
 * @author Giuseppe
 * @author Graziana
 */
public class GestoreSchermate {
    public static final String MENU     = "menu";
    public static final String PROVA1   = "prova1";
    public static final String GAME     = "game";
    public static String current_card = "";

    private final GameUIListenerImpl listener;
    private final CardLayout cardLayout;
    private final JPanel contenitore;
    private final JLayeredPane layeredPane;
    private final JPanel sfondoScurito;
    private final InventarioPanel inventarioPanel;
    private final PausaPanel pausePanel;
    private final QuestPanel questPanel;

    public GestoreSchermate(JFrame frame, GameManager gameManager) {
        layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        // ===== layer base: CardLayout con le schermate normali =====
        cardLayout = new CardLayout();
        contenitore = new JPanel(cardLayout);
        layeredPane.add(contenitore, JLayeredPane.DEFAULT_LAYER);

        // aggiungi le schermate
        addSchermata(MENU, new MenuIniziale(this, gameManager));
        addSchermata(PROVA1, new Prova1(gameManager));
        addSchermata(GAME, new GamePanel(gameManager));

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
        sfondoScurito.addMouseListener(new java.awt.event.MouseAdapter() {
            //vuoto per intercettare tutti gli eventi e prevenire input in altri punti della GUI
        });
        layeredPane.add(sfondoScurito, JLayeredPane.PALETTE_LAYER);

        //TUTTI I PANEL
        inventarioPanel = new InventarioPanel(gameManager);
        inventarioPanel.setVisible(false);
        layeredPane.add(inventarioPanel, JLayeredPane.MODAL_LAYER);
        inventarioPanel.getBtnChiudi().addActionListener(e -> chiudiInventario());

        pausePanel = new PausaPanel(this, gameManager);
        pausePanel.setVisible(false);
        layeredPane.add(pausePanel, JLayeredPane.POPUP_LAYER);
        pausePanel.getBtnChiudi().addActionListener(e -> chiudiPausa());

        questPanel = new QuestPanel(gameManager);
        questPanel.setVisible(true);
        layeredPane.add(questPanel, JLayeredPane.MODAL_LAYER);

        // ===== gestione input =====
        //        NON SPOSTARE

        //il listener collega gli eventi di GameManager alla GUI (menu, inventario, ecc.)
        this.listener = new GameUIListenerImpl(this);
        gameManager.collegaGUI(listener);

        InputMap inputMap = contenitore.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contenitore.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("E"), "apriInventario");
        actionMap.put("apriInventario", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inventarioPanel.isVisible() && !pausePanel.isVisible()) {
                    if (current_card.equals(MENU)) return;
                    apriInventario();
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "apriPausa");
        actionMap.put("apriPausa", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pausePanel.isVisible() && !inventarioPanel.isVisible()) {
                    //nasconde il pulsante menù se ci troviamo già in questa schermata
                    pausePanel.getBtnMenu().setVisible(!current_card.equals(MENU));
                    apriPausa();
                }
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

        int invW = 700, invH = 500;
        inventarioPanel.setBounds((w - invW) / 2, (h - invH) / 2, invW, invH);

        int pauW = 350, pauH = 420; // aumentato per contenere titolo + 4 bottoni
        pausePanel.setBounds((w - pauW) / 2, (h - pauH) / 2, pauW, pauH);

        int questW = 300;
        int margine = 5;

        questPanel.setBounds(margine + 5, (layeredPane.getHeight() / 2) - 150, questW, questPanel.getPreferredSize().height);
    }

    public void addSchermata(String nome, JPanel schermata) {
        contenitore.add(nome, schermata);
    }

    public void mostra(String nome) {
        current_card = nome;
        questPanel.setVisible(!nome.equals(MENU));

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

    public void apriPausa() {
        pausePanel.init();
        sfondoScurito.setVisible(true);
        pausePanel.setVisible(true);
        layeredPane.moveToFront(pausePanel);
    }

    public void chiudiPausa() {
        pausePanel.setVisible(false);
        sfondoScurito.setVisible(false);
    }

    private void apriQuest() {
        questPanel.setVisible(true);
    }

    private void chiudiQuest() {
        questPanel.setVisible(false);
    }

    public QuestPanel getQuestPanel() {
        return questPanel;
    }

    public InventarioPanel getInventarioPanel() {
        return inventarioPanel;
    }

    public PausaPanel getPausePanel() {
        return pausePanel;
    }
}