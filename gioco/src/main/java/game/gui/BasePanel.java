package game.gui;

import engine.manager.BaseGameManager;
import game.manager.GameManager;

import javax.swing.JPanel;

public abstract class BasePanel extends JPanel {
    protected final GameManager gameManager;

    public BasePanel(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    public abstract void init();
    public abstract void aggiorna();
    public abstract void reset();
}
