package game.gui;

import game.manager.GameManager;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SalvataggioHelper {
    public static void gestisciSalvataggio(Component parent, GameManager gameManager) {
        java.util.List<Integer> lista = new ArrayList<>(gameManager.getSaveManager().listaSalvataggi());
        int slot = chiediSlot(parent, lista, "Salva");
        if (slot == -1) return;
        try {
            gameManager.salvaPartita(slot);
            JOptionPane.showMessageDialog(parent, "Salvataggio completato nello slot: " + slot, "Salvataggio", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Errore durante il salvataggio: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void gestisciCarica(Component parent, GameManager gameManager, GestoreSchermate gestoreSchermate) {
        List<Integer> lista = new ArrayList<>(gameManager.getSaveManager().listaSalvataggi());
        int slot = chiediSlot(parent, lista, "Carica");
        if (slot == -1) return;
        try {
            gameManager.caricaPartita(slot);
            JOptionPane.showMessageDialog(parent, "Caricamento completato dello slot: " + slot, "Caricamento", JOptionPane.INFORMATION_MESSAGE);
            aggiornaPanel(gestoreSchermate);
            gestoreSchermate.mostra(GestoreSchermate.GAME);

        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Errore durante il caricamento: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void aggiornaPanel(GestoreSchermate gestoreSchermate) {
        gestoreSchermate.getInventarioPanel().aggiorna();
        gestoreSchermate.getQuestPanel().aggiorna();
    }

    private static int chiediSlot(Component parent, List<Integer> lista, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);

        JPanel contenuto = new JPanel();
        contenuto.setLayout(new BoxLayout(contenuto, BoxLayout.Y_AXIS));
        contenuto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (!lista.isEmpty()) {
            JLabel etichettaSlotEsistenti = new JLabel("Slot esistenti: " + lista.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(""));
            etichettaSlotEsistenti.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenuto.add(etichettaSlotEsistenti);
            contenuto.add(Box.createVerticalStrut(15));
        }

        JLabel etichettaInput = new JLabel("Inserisci il numero di slot:");
        etichettaInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenuto.add(etichettaInput);
        contenuto.add(Box.createVerticalStrut(10));

        //FORMATTER per il campo di input
        NumberFormatter formatter = getNumberFormatter();

        JFormattedTextField inputSlot = new JFormattedTextField(formatter);
        inputSlot.setColumns(5);

        inputSlot.setHorizontalAlignment(JFormattedTextField.CENTER);
        inputSlot.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputSlot.setMaximumSize(new Dimension(100, 28));
        contenuto.add(inputSlot);
        contenuto.add(Box.createVerticalStrut(20));

        // bottoni conferma/annulla
        JPanel pannelloBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnConferma = new JButton("Conferma");
        JButton btnAnnulla = new JButton("Annulla");
        pannelloBottoni.add(btnConferma);
        pannelloBottoni.add(btnAnnulla);
        contenuto.add(pannelloBottoni);

        final int[] risultato = {-1};

        btnConferma.addActionListener(e -> {
            String testo = inputSlot.getText();
            if (!testo.isBlank()) {
                risultato[0] = Integer.parseInt(testo);
            }
            dialog.dispose();
        });

        btnAnnulla.addActionListener(e -> dialog.dispose());

        dialog.add(contenuto);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true); // blocca qui finché non si chiude

        return risultato[0];
    }

    private static NumberFormatter getNumberFormatter() {
        NumberFormat formato = NumberFormat.getIntegerInstance();
        formato.setGroupingUsed(false);

        NumberFormatter formatter = new NumberFormatter(formato) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.isEmpty()) {
                    return null; // permette il campo vuoto senza lanciare eccezione
                }
                return super.stringToValue(text);
            }
        };
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setAllowsInvalid(false); // ora permette stati intermedi (incluso vuoto)
        return formatter;
    }
}
