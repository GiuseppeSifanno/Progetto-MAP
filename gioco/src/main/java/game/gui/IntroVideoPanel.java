package game.gui;

import game.manager.GameManager;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.awt.*;

public class IntroVideoPanel extends BasePanel {

    private final JFXPanel fxPanel;
    private MediaPlayer mediaPlayer;
    private final Runnable alTermine;
    private boolean terminato = false;

    public IntroVideoPanel(GameManager gameManager, Runnable alTermine) {
        super(gameManager);
        this.alTermine = alTermine;

        this.fxPanel = new JFXPanel(); // inizializza il toolkit JavaFX internamente
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
    }

    @Override
    public void init() {
        // Tutto il codice JavaFX deve girare sul JavaFX Application Thread,
        // non sull'Event Dispatch Thread di Swing.
        Platform.runLater(this::creaScenaVideo);
    }

    @Override
    public void aggiorna() {
    }

    private void creaScenaVideo() {

        var risorsa = getClass().getResource("/assets/naufragio.mp4");

        if (risorsa == null) {
            throw new RuntimeException(
                    "Impossibile trovare il video: /assets/naufragio.mp4"
            );
        }

        Media media = new Media(risorsa.toExternalForm());

        mediaPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root);

        fxPanel.setScene(scene);

        mediaPlayer.setOnReady(() -> {

            System.out.println("Video pronto");
            System.out.println("Durata: " +
                    mediaPlayer.getTotalDuration());

            // Dimensioni fisse per test
            mediaView.setFitWidth(1280);
            mediaView.setFitHeight(720);

            mediaPlayer.play();
        });

        mediaPlayer.setOnError(() -> {
            System.err.println("Errore MediaPlayer:");
            System.err.println(mediaPlayer.getError());
        });

        mediaPlayer.setOnEndOfMedia(this::terminaIntro);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                terminaIntro();
            }
        });
    }

    public void skipIntro() {
        terminaIntro();
    }

    private void terminaIntro() {
        if (terminato) {
            return;
        }

        terminato = true;

        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.stop());
        }

        javax.swing.SwingUtilities.invokeLater(alTermine);
    }

    public void stop() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> mediaPlayer.stop());
        }
    }

    @Override
    public void reset() {
        terminato = false;

        if (mediaPlayer != null) {
            Platform.runLater(() -> {
                mediaPlayer.seek(javafx.util.Duration.ZERO);
                mediaPlayer.play();
            });
        }
    }
}