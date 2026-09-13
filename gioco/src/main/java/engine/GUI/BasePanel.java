package engine.GUI;

import game.manager.GameManager;

import javax.swing.*;

/**
 * BasePanel è la classe base per tutti i pannelli grafici dell'engine.
 */
public abstract class BasePanel extends JPanel {
    protected final GameManager gameManager;
    protected InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    protected ActionMap actionMap = getActionMap();

    /**
     * Costruttore di BasePanel.
     * @param gameManager gestore del gioco
     */
    public BasePanel(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public abstract void init();
    public abstract void aggiorna();
    public abstract void reset();
}
