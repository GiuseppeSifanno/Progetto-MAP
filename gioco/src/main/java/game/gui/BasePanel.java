package game.gui;

import engine.manager.BaseGameManager;

import javax.swing.JPanel;

public abstract class BasePanel extends JPanel {
    protected final BaseGameManager gameManager;

    public BasePanel(BaseGameManager gameManager) {
        this.gameManager = gameManager;
    }
    public abstract void init();
    public abstract void aggiorna();
    public abstract void reset();
}
