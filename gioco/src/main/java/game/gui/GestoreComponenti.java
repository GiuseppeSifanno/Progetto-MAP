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
import java.awt.RenderingHints;

/**
 * Gestisce posizione, ridimensionamento e stato visivo (hover/click) di
 * bottoni e altri componenti sopra un PannelloSfondo.
 *
 * Ogni bottone registrato mostra automaticamente un effetto quando il
 * mouse ci passa sopra (leggermente più chiaro) e quando viene premuto
 * (leggermente più scuro), partendo dalla sua icona originale.
 */

public class GestoreComponenti {

    private static class Elemento {
        final JComponent componente;
        final int centroX, centroY, larghezza, altezza;

        // Immagini originali a piena qualità, prese dalle Properties di NetBeans
        Image iconaNormale;
        Image iconaRollover;
        Image iconaPremuta;

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
     * Registra un componente. Se è un JButton, recupera automaticamente le
     * icone normale/rollover/pressed già impostate dalle Properties di
     * NetBeans (icon, rolloverIcon, pressedIcon) e le ridimensiona insieme
     * al bottone ad ogni resize. Swing gestisce da solo il cambio icona
     * su hover/click, non serve altro codice.
     */
    public void registra(JComponent componente, int centroX, int centroY,
                          int larghezza, int altezza) {
        Elemento elemento = new Elemento(componente, centroX, centroY, larghezza, altezza);

        if (componente instanceof JButton) {
            JButton bottone = (JButton) componente;
            impostaBottoneTrasparente(bottone);

            elemento.iconaNormale = estraiImmagine(bottone.getIcon());
            elemento.iconaRollover = estraiImmagine(bottone.getRolloverIcon());
            elemento.iconaPremuta = estraiImmagine(bottone.getPressedIcon());
        }

        sfondo.add(componente);
        elementi.add(elemento);
        riposiziona(elemento);
    }

    private Image estraiImmagine(Icon icona) {
        return (icona instanceof ImageIcon) ? ((ImageIcon) icona).getImage() : null;
    }

    public void rimuovi(JComponent componente) {
        for (int i = 0; i < elementi.size(); i++) {
            if (elementi.get(i).componente == componente) {
                elementi.remove(i);
                break;
            }
        }
        sfondo.remove(componente);
    }

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

    private void riposiziona(Elemento elemento) {
        double scalaX = sfondo.getScalaX();
        double scalaY = sfondo.getScalaY();

        int w = (int) (elemento.larghezza * scalaX);
        int h = (int) (elemento.altezza * scalaY);

        Point centro = sfondo.convertiCoordinataImmagine(elemento.centroX, elemento.centroY);
        elemento.componente.setBounds(centro.x - w / 2, centro.y - h / 2, w, h);

        if (elemento.componente instanceof JButton && w > 0 && h > 0) {
            JButton bottone = (JButton) elemento.componente;

            if (elemento.iconaNormale != null) {
                bottone.setIcon(scala(elemento.iconaNormale, w, h));
            }
            if (elemento.iconaRollover != null) {
                bottone.setRolloverIcon(scala(elemento.iconaRollover, w, h));
            }
            if (elemento.iconaPremuta != null) {
                bottone.setPressedIcon(scala(elemento.iconaPremuta, w, h));
            }
        }
    }

    /** Scala un'immagine in modalità "riempi": copre w x h mantenendo le proporzioni, tagliando l'eccesso. */
    private ImageIcon scala(Image originale, int w, int h) {
        int imgW = originale.getWidth(null);
        int imgH = originale.getHeight(null);
        if (imgW <= 0 || imgH <= 0) return new ImageIcon(originale);

        double fattore = Math.max((double) w / imgW, (double) h / imgH);
        int nuovaW = (int) (imgW * fattore);
        int nuovaH = (int) (imgH * fattore);
        int offsetX = (w - nuovaW) / 2;
        int offsetY = (h - nuovaH) / 2;

        BufferedImage risultato = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = risultato.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setClip(0, 0, w, h);
        g2.drawImage(originale, offsetX, offsetY, nuovaW, nuovaH, null);
        g2.dispose();

        return new ImageIcon(risultato);
    }
}