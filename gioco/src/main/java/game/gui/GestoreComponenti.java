/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.gui;

/**
 *
 * @author User
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * Gestisce posizione, ridimensionamento e stato visivo (hover/click) di
 * bottoni e altri componenti sopra un PannelloSfondo.
 *
 * Ogni bottone registrato mostra automaticamente un effetto quando il
 * mouse ci passa sopra (leggermente più chiaro) e quando viene premuto
 * (leggermente più scuro), partendo dalla sua icona originale.
 */
public class GestoreComponenti {

    /** Componente + le sue coordinate/dimensioni originali + stato visivo. */
    private static class Elemento {
        final JComponent componente;
        final int centroX, centroY, larghezza, altezza;

        // Solo per i bottoni: icona originale a piena qualità e stato mouse
        Image iconaOriginale;
        boolean inHover = false;
        boolean premuto = false;

        Elemento(JComponent componente, int centroX, int centroY, int larghezza, int altezza) {
            this.componente = componente;
            this.centroX = centroX;
            this.centroY = centroY;
            this.larghezza = larghezza;
            this.altezza = altezza;
        }
    }

    private final PannelloSfondo sfondo;
    private final List<Elemento> elementi = new ArrayList<>();

    public GestoreComponenti(PannelloSfondo sfondo) {
        this.sfondo = sfondo;

        sfondo.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                riposizionaTutti();
            }
        });
    }

    /**
     * Registra un componente: lo aggiunge al pannello, lo posiziona subito,
     * e lo riposiziona automaticamente ad ogni resize. Se è un JButton,
     * viene reso trasparente e reagisce visivamente a hover/click.
     */
    public void registra(JComponent componente, int centroX, int centroY,
                          int larghezza, int altezza) {
        Elemento elemento = new Elemento(componente, centroX, centroY, larghezza, altezza);

        if (componente instanceof JButton) {
            JButton bottone = (JButton) componente;
            impostaBottoneTrasparente(bottone);

            Icon icona = bottone.getIcon();
            if (icona instanceof ImageIcon) {
               elemento.iconaOriginale = ((ImageIcon) icona).getImage();
            }

            aggiungiEffettiMouse(bottone, elemento);
        }

        sfondo.add(componente);
        elementi.add(elemento);
        riposiziona(elemento);
    }

    /** Smette di gestire un componente e lo rimuove dal pannello. */
    public void rimuovi(JComponent componente) {
        for (int i = 0; i < elementi.size(); i++) {
            if (elementi.get(i).componente == componente) {
                elementi.remove(i);
                break;
            }
        }
        sfondo.remove(componente);
    }

    /** Ricalcola subito la posizione (e l'icona) di tutti i componenti registrati. */
    public void riposizionaTutti() {
        for (Elemento elemento : elementi) {
            riposiziona(elemento);
        }
        sfondo.revalidate();
        sfondo.repaint();
    }

    private void impostaBottoneTrasparente(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setText("");
    }

    /** Collega gli eventi mouse che aggiornano hover/premuto e ridisegnano l'icona. */
    private void aggiungiEffettiMouse(JButton bottone, Elemento elemento) {
        bottone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                elemento.inHover = true;
                aggiornaIcona(elemento);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                elemento.inHover = false;
                elemento.premuto = false;
                aggiornaIcona(elemento);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                elemento.premuto = true;
                aggiornaIcona(elemento);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                elemento.premuto = false;
                aggiornaIcona(elemento);
            }
        });
    }

    private void riposiziona(Elemento elemento) {
        double scalaX = sfondo.getScalaX();
        double scalaY = sfondo.getScalaY();

        int w = (int) (elemento.larghezza * scalaX);
        int h = (int) (elemento.altezza * scalaY);

        Point centro = sfondo.convertiCoordinataImmagine(elemento.centroX, elemento.centroY);

        elemento.componente.setBounds(centro.x - w / 2, centro.y - h / 2, w, h);

        if (elemento.iconaOriginale != null) {
            aggiornaIcona(elemento);
        }
    }

    /** Ridisegna l'icona del bottone alla dimensione attuale, applicando l'effetto giusto. */
    private void aggiornaIcona(Elemento elemento) {
        if (elemento.iconaOriginale == null) return;

        JButton bottone = (JButton) elemento.componente;
        int w = bottone.getWidth();
        int h = bottone.getHeight();
        if (w <= 0 || h <= 0) return;

        Image scalata = elemento.iconaOriginale.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        BufferedImage risultato = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = risultato.createGraphics();
        g2.drawImage(scalata, 0, 0, null);

        if (elemento.premuto) {
            // Effetto "cliccato": scurisce leggermente
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
        } else if (elemento.inHover) {
            // Effetto "selezionato": schiarisce leggermente
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
        }

        g2.dispose();
        bottone.setIcon(new ImageIcon(risultato));
    }
}