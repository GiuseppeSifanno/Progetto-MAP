package game.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.RenderingHints;

public class GestoreComponenti {

    private static class Elemento {

        final JComponent componente;

        // Coordinate sull'immagine originale
        final int centroX;
        final int centroY;
        final int larghezza;
        final int altezza;

        // Tipo di posizionamento
        final boolean centratoInBasso;

        // Distanza dal bordo inferiore del pannello
        final int margineInferiore;

        // Immagini originali del bottone
        Image iconaNormale;
        Image iconaRollover;
        Image iconaPremuta;

        Elemento(
                JComponent componente,
                int centroX,
                int centroY,
                int larghezza,
                int altezza
        ) {
            this(
                    componente,
                    centroX,
                    centroY,
                    larghezza,
                    altezza,
                    false,
                    0
            );
        }

        Elemento(
                JComponent componente,
                int centroX,
                int centroY,
                int larghezza,
                int altezza,
                boolean centratoInBasso,
                int margineInferiore
        ) {
            this.componente = componente;
            this.centroX = centroX;
            this.centroY = centroY;
            this.larghezza = larghezza;
            this.altezza = altezza;
            this.centratoInBasso = centratoInBasso;
            this.margineInferiore = margineInferiore;
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
     * Registra un componente utilizzando coordinate riferite
     * all'immagine originale.
     *
     * Questo metodo va utilizzato per gli hotspot.
     */
    public void registra(
            JComponent componente,
            int centroX,
            int centroY,
            int larghezza,
            int altezza
    ) {

        Elemento elemento = new Elemento(
                componente,
                centroX,
                centroY,
                larghezza,
                altezza
        );

        preparaComponente(componente, elemento);

        sfondo.add(componente);
        elementi.add(elemento);

        riposiziona(elemento);
    }

    /**
     * Registra un componente centrato orizzontalmente
     * e ancorato al bordo inferiore dello sfondo.
     *
     * @param componente componente da registrare
     * @param larghezza larghezza del componente
     * @param altezza altezza del componente
     * @param margineInferiore distanza dal bordo inferiore
     */
    public void registraCentratoInBasso(
            JComponent componente,
            int larghezza,
            int altezza,
            int margineInferiore
    ) {

        Elemento elemento = new Elemento(
                componente,
                0,
                0,
                larghezza,
                altezza,
                true,
                margineInferiore
        );

        // Non trattiamo il DialogBox come un normale JButton.
        // Se invece fosse un JButton, questa preparazione
        // funzionerebbe comunque.
        preparaComponente(componente, elemento);

        sfondo.add(componente);
        elementi.add(elemento);

        riposiziona(elemento);
    }

    /**
     * Prepara il componente.
     */
    private void preparaComponente(
            JComponent componente,
            Elemento elemento
    ) {

        if (componente instanceof JButton) {

            JButton bottone = (JButton) componente;

            impostaBottoneTrasparente(bottone);

            elemento.iconaNormale =
                    estraiImmagine(bottone.getIcon());

            elemento.iconaRollover =
                    estraiImmagine(bottone.getRolloverIcon());

            elemento.iconaPremuta =
                    estraiImmagine(bottone.getPressedIcon());
        }
    }

    private Image estraiImmagine(Icon icona) {

        return (icona instanceof ImageIcon)
                ? ((ImageIcon) icona).getImage()
                : null;
    }

    /**
     * Rimuove un componente dal gestore e dal pannello.
     */
    public void rimuovi(JComponent componente) {

        for (int i = 0; i < elementi.size(); i++) {

            if (elementi.get(i).componente == componente) {
                elementi.remove(i);
                break;
            }
        }

        sfondo.remove(componente);

        sfondo.revalidate();
        sfondo.repaint();
    }

    /**
     * Riposiziona tutti i componenti.
     *
     * Viene chiamato automaticamente quando
     * PannelloSfondo cambia dimensione.
     */
    public void riposizionaTutti() {

        for (Elemento elemento : elementi) {
            riposiziona(elemento);
        }

        sfondo.revalidate();
        sfondo.repaint();
    }

    /**
     * Riposiziona un singolo componente.
     */
    private void riposiziona(Elemento elemento) {

        /*
         * ============================================================
         * COMPONENTE CENTRATO IN BASSO
         * ============================================================
         */
        if (elemento.centratoInBasso) {

            int panelW = sfondo.getWidth();
            int panelH = sfondo.getHeight();

            /*
             * La larghezza viene adattata alla dimensione
             * della finestra, ma con un limite massimo.
             */
            int larghezzaMassima =
                    Math.min(
                            elemento.larghezza,
                            panelW - 40
                    );

            int altezza =Math.min(elemento.altezza, panelH);

            /*
             * Centro orizzontale.
             */
            int x = (panelW - larghezzaMassima) / 2;

            /*
             * Ancoraggio al fondo.
             */
            int y =panelH - altezza - elemento.margineInferiore;

            elemento.componente.setBounds(
                    x,
                    y,
                    larghezzaMassima,
                    altezza
            );

            return;
        }

        /*
         * ============================================================
         * COMPONENTE LEGATO ALL'IMMAGINE
         * ============================================================
         */

        double scalaX = sfondo.getScalaX();
        double scalaY = sfondo.getScalaY();

        int w = (int) (elemento.larghezza * scalaX);
        int h = (int) (elemento.altezza * scalaY);

        Point centro =
                sfondo.convertiCoordinataImmagine(
                        elemento.centroX,
                        elemento.centroY
                );

        elemento.componente.setBounds(
                centro.x - w / 2,
                centro.y - h / 2,
                w,
                h
        );

        /*
         * Ridimensionamento delle icone dei JButton.
         */
        if (
                elemento.componente instanceof JButton
                        && w > 0
                        && h > 0
        ) {

            JButton bottone =
                    (JButton) elemento.componente;

            if (elemento.iconaNormale != null) {

                bottone.setIcon(
                        scala(
                                elemento.iconaNormale,
                                w,
                                h
                        )
                );
            }

            if (elemento.iconaRollover != null) {

                bottone.setRolloverIcon(
                        scala(
                                elemento.iconaRollover,
                                w,
                                h
                        )
                );
            }

            if (elemento.iconaPremuta != null) {

                bottone.setPressedIcon(
                        scala(
                                elemento.iconaPremuta,
                                w,
                                h
                        )
                );
            }
        }
    }

    /**
     * Rende un JButton completamente trasparente.
     */
    private void impostaBottoneTrasparente(JButton btn) {

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setText("");
    }

    /**
     * Scala un'immagine in modalità "riempi".
     */
    private ImageIcon scala(
            Image originale,
            int w,
            int h
    ) {

        int imgW = originale.getWidth(null);
        int imgH = originale.getHeight(null);

        if (imgW <= 0 || imgH <= 0) {
            return new ImageIcon(originale);
        }

        double fattore =
                Math.max(
                        (double) w / imgW,
                        (double) h / imgH
                );

        int nuovaW =
                (int) (imgW * fattore);

        int nuovaH =
                (int) (imgH * fattore);

        int offsetX =
                (w - nuovaW) / 2;

        int offsetY =
                (h - nuovaH) / 2;

        BufferedImage risultato =
                new BufferedImage(
                        w,
                        h,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                risultato.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        g2.setClip(0, 0, w, h);

        g2.drawImage(
                originale,
                offsetX,
                offsetY,
                nuovaW,
                nuovaH,
                null
        );

        g2.dispose();

        return new ImageIcon(risultato);
    }
}