package game.gui;

import game.manager.GameManager;
import game.model.PassoQuestCompletato;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuestPanel extends BasePanel {

    private static final Color COLORE_BORDO = new Color(198, 156, 109);
    private static final Color COLORE_SFONDO_PANNELLO = new Color(45, 33, 27, 180);

    public QuestPanel(GameManager gameManager) {
        super(gameManager);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JPanel questList = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(COLORE_SFONDO_PANNELLO);
                g2.fillRoundRect(
                        0, 0,
                        getWidth(), getHeight(),
                        20, 20
                );

                g2.dispose();
            }
        };

        questList.setOpaque(false);
        questList.setLayout(new BoxLayout(questList, BoxLayout.Y_AXIS));

        questList.setBorder(BorderFactory.createCompoundBorder(
                new BordoArrotondato(20, COLORE_BORDO),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        questList.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (PassoQuestCompletato quest : recuperaQuest()) {
            questList.add(disegnaQuestComponent(quest));
            questList.add(Box.createVerticalStrut(10));
        }

        add(questList);
    }

    private List<PassoQuestCompletato> recuperaQuest() {
        return gameManager.getGameState().getPassiQuestCompletati();
    }

    private JPanel disegnaQuestComponent(PassoQuestCompletato quest) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel questLabel = new JLabel("Quest " + quest.idQuest());
        questLabel.setForeground(Color.WHITE);
        JLabel statoLabel = new JLabel("Stato");
        statoLabel.setForeground(Color.WHITE);

        panel.add(questLabel);
        panel.add(statoLabel);

        return panel;
    }

    @Override
    public void init() {
    }

    @Override
    public void aggiorna() {
    }

    @Override
    public void reset() {
    }
}