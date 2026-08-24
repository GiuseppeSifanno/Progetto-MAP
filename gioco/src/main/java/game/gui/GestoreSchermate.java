/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.gui;

import game.manager.GameManager;
import game.ui.GameUIListener;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Graziana
 * @author Giuseppe
 */
public class GestoreSchermate {
    public static final String MENU = "menu";
    public static final String PROVA1 = "prova1";

    private static GameUIListener listener;
    private final CardLayout cardLayout;
    private final JPanel contenitore;

    public GestoreSchermate(JFrame frame, GameManager gameManager) {
        this.cardLayout = new CardLayout();
        this.contenitore = new JPanel(cardLayout);
        frame.setContentPane(contenitore);

        //il listener viene agganciato all'interno della funzione
        gameManager.collegaGUI(listener);

        //aggiunta di tutti i frame nel contenitore
        addSchermata(MENU, new MenuIniziale(this, gameManager));
        addSchermata(PROVA1, new Prova1(gameManager));
    }

    public void addSchermata(String nome, JPanel schermata){
        contenitore.add(nome, schermata);
    }

    /** Mostra la schermata registrata con questo nome, nascondendo quella attuale. */
    public void mostra(String nome) {
        cardLayout.show(contenitore, nome);
    }
}
