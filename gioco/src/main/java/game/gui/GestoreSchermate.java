/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.gui;

/**
 *
 * @author User
 */
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GestoreSchermate {
    
    public static final String MENU = "menu";
    public static final String PROVA1 = "prova1";
    
    private final CardLayout cardLayout;
    private final JPanel contenitore;

    public GestoreSchermate(JFrame frame) {
        this.cardLayout = new CardLayout();
        this.contenitore = new JPanel(cardLayout);
        frame.setContentPane(contenitore);
        
        inizializzaSchermate();
    }
    
    private void inizializzaSchermate() {

        MenuIniziale menu = new MenuIniziale(this);
        Prova1 prova1 = new Prova1();

        contenitore.add(menu, MENU);
        contenitore.add(prova1, PROVA1);
    }

    /** Mostra la schermata registrata con questo nome, nascondendo quella attuale. */
    public void mostra(String nome) {
        cardLayout.show(contenitore, nome);
    }
}
