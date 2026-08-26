package game.gui;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class BordoArrotondato extends AbstractBorder {
    private final int raggio;
    private final int spessore;
    private final Color colore;

    /**
     * @param raggio raggio degli angoli arrotondati
     * @param spessore spessore della linea del bordo
     * @param colore colore del bordo
     */
    public BordoArrotondato(int raggio, int spessore, Color colore) {
        this.raggio = raggio;
        this.spessore = spessore;
        this.colore = colore;
    }

    /**
     * Costruttore di comodo con spessore di default (2px).
     */
    public BordoArrotondato(int raggio, Color colore) {
        this(raggio, 2, colore);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colore);
        g2.setStroke(new BasicStroke(spessore));

        int offset = spessore / 2;
        g2.drawRoundRect(
                x + offset, y + offset,
                width - spessore, height - spessore,
                raggio, raggio
        );
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int margine = spessore + raggio / 4;
        return new Insets(margine, margine, margine, margine);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets calcolati = getBorderInsets(c);
        insets.top = calcolati.top;
        insets.left = calcolati.left;
        insets.bottom = calcolati.bottom;
        insets.right = calcolati.right;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false; // permette di vedere ciò che sta dietro gli angoli tagliati fuori dall'arco
    }
}