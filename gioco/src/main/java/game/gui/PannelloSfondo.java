/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.gui;

/**
 *
 * @author User
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PannelloSfondo extends JPanel{

    private BufferedImage immagine;
    private Color coloreSfondo = Color.BLACK;

    public PannelloSfondo() {
        setLayout(null);   // permette di posizionare i figli manualmente
        setOpaque(true);
    }

    public PannelloSfondo(String percorso) {
        this();
        setImmagineSfondo(percorso);
    }

    /** Carica l'immagine di sfondo dal classpath, es. "/assets/Menu.png". */
    public void setImmagineSfondo(String percorso) {
        java.net.URL url = getClass().getResource(percorso);
        if (url == null) {
            System.err.println("PannelloSfondo: immagine non trovata: " + percorso);
            immagine = null;
            repaint();
            return;
        }
        try {
            immagine = ImageIO.read(url);
            repaint();
        } catch (IOException e) {
            System.err.println("PannelloSfondo: errore caricamento immagine: " + percorso);
            e.printStackTrace();
        }
    }

    /** Dimensioni originali dell'immagine caricata, o null se non ancora caricata. */
    public Dimension getDimensioniOriginali() {
        if (immagine == null) return null;
        return new Dimension(immagine.getWidth(), immagine.getHeight());
    }

    /**
     * Area (posizione + dimensione) in cui l'immagine viene effettivamente
     * disegnata dentro il pannello, in base alla modalità di scala attiva.
     */
    public Rectangle getAreaImmagine() {
        if (immagine == null) {
            return new Rectangle(0, 0, getWidth(), getHeight());
        }

        int panelW = getWidth();
        int panelH = getHeight();
        int imgW = immagine.getWidth();
        int imgH = immagine.getHeight();

        double scala = Math.min((double) panelW / imgW, (double) panelH / imgH);
        int nuovaW = (int) (imgW * scala);
        int nuovaH = (int) (imgH * scala);
        int x = (panelW - nuovaW) / 2;
        int y = (panelH - nuovaH) / 2;

        return new Rectangle(x, y, nuovaW, nuovaH);
    }

    /** Fattore di scala orizzontale attuale rispetto all'immagine originale. */
    public double getScalaX() {
        if (immagine == null) return 1.0;
        return (double) getAreaImmagine().width / immagine.getWidth();
    }

    /** Fattore di scala verticale attuale rispetto all'immagine originale. */
    public double getScalaY() {
        if (immagine == null) return 1.0;
        return (double) getAreaImmagine().height / immagine.getHeight();
    }

    /**
     * Converte una coordinata (x, y) misurata sull'immagine ORIGINALE
     * (es. con Paint) nella posizione reale dentro il pannello ridimensionato.
     */
    public Point convertiCoordinataImmagine(int xOriginale, int yOriginale) {
        if (immagine == null) return new Point(xOriginale, yOriginale);
        Rectangle area = getAreaImmagine();
        return new Point(area.x + (int) (xOriginale * getScalaX()),
                          area.y + (int) (yOriginale * getScalaY()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(coloreSfondo);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (immagine != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                 RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            Rectangle area = getAreaImmagine();
            g2.drawImage(immagine, area.x, area.y, area.width, area.height, this);
        }

        g2.dispose();
    }
}
