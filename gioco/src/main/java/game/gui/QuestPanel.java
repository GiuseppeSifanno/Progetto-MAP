package game.gui;

import game.manager.GameManager;
import game.model.PassoQuest;
import game.model.PassoQuestCompletato;
import game.model.Quest;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestPanel extends BasePanel {
    private static final Color COLORE_BORDO = new Color(198, 156, 109);
    private static final Color COLORE_SFONDO_PANNELLO = new Color(45, 33, 27, 180);
    private JPanel questList;

    public QuestPanel(GameManager gameManager) {
        super(gameManager);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        questList = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLORE_SFONDO_PANNELLO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
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

        aggiornaQuestLabel();
        add(questList);
    }

    /**
     * @return set degli id passo già completati
     */
    private Set<String> recuperaPassiCompletati() {
        return gameManager.getGameState().getPassiQuestCompletati().stream()
                .map(PassoQuestCompletato::idPasso)
                .collect(Collectors.toSet());
    }

    /**
     * Trova la prima quest con un passo non ancora completato, scorrendo
     * le quest in ordine. Se tutte le quest sono completate, restituisce null.
     */
    private PassoQuest trovaPassoAttuale(Quest[] questTrovataOut) {
        Set<String> completati = recuperaPassiCompletati();

        for (Quest quest : gameManager.getQuest().values()) {
            for (PassoQuest passo : quest.getPassi()) {
                if (!completati.contains(passo.idPasso())) {
                    questTrovataOut[0] = quest;
                    return passo;
                }
            }
        }
        return null; // tutte le quest completate
    }

    private void aggiornaQuestLabel() {
        questList.removeAll();

        Quest[] questTrovata = new Quest[1]; // "contenitore" per far uscire anche la Quest dal metodo
        PassoQuest passoAttuale = trovaPassoAttuale(questTrovata);

        if (passoAttuale != null) {
            JLabel titoloQuest = new JLabel(questTrovata[0].getNome());
            titoloQuest.setFont(new Font("SansSerif", Font.BOLD, 22));
            titoloQuest.setForeground(Color.WHITE);
            titoloQuest.setAlignmentX(Component.CENTER_ALIGNMENT);
            questList.add(titoloQuest);

            questList.add(Box.createVerticalStrut(10));

            JTextArea testoPasso = new JTextArea(passoAttuale.testo());
            testoPasso.setLineWrap(true);
            testoPasso.setWrapStyleWord(true);
            testoPasso.setEditable(false);
            testoPasso.setOpaque(false);
            testoPasso.setFocusable(false);
            testoPasso.setFont(new Font("SansSerif", Font.PLAIN, 16));
            testoPasso.setForeground(Color.WHITE);
            testoPasso.setAlignmentX(Component.CENTER_ALIGNMENT);
            //testoPasso.setMaximumSize(new Dimension(280, 100)); // larghezza max, altezza generosa
            questList.add(testoPasso);
        } else {
            JLabel completato = new JLabel("Tutte le quest completate");
            completato.setForeground(Color.WHITE);
            completato.setAlignmentX(Component.CENTER_ALIGNMENT);
            questList.add(completato);
        }

        questList.revalidate();
        questList.repaint();
    }

    @Override
    public void init() {
        aggiorna();
    }

    @Override
    public void aggiorna() {
        aggiornaQuestLabel();
    }

    @Override
    public void reset() {
        questList.removeAll();
        questList.revalidate();
        questList.repaint();
    }
}