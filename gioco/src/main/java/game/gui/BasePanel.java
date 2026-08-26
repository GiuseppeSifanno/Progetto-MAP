package game.gui;

import engine.manager.BaseGameManager;
import game.manager.GameManager;

import javax.swing.*;

public abstract class BasePanel extends JPanel {
    protected final GameManager gameManager;
    protected InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    protected ActionMap actionMap = getActionMap();

    public BasePanel(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    public abstract void init();
    public abstract void aggiorna();
    public abstract void reset();
}
