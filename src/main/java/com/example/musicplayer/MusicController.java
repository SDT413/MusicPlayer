package com.example.musicplayer;

import com.almasb.fxgl.entity.action.Action;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MusicController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button play;
    @FXML
    private Button pause;
    @FXML
    private Button reset;
    @FXML
    private Button next;
    @FXML
    private Button previous;
    @FXML
    private ComboBox<Integer> speed;
    @FXML
    private Slider volume;
    @FXML
    private ProgressBar songBar;
    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private int currentSong;
    private Integer[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
    private Timer timer;
    private TimerTask timerTask;
    private boolean isTimerRunning = false;
    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<>();
        directory = new File("src/main/java/com/example/musicplayer/songs");
        files = directory.listFiles();
        if (files != null) {
            Collections.addAll(songs, files);
        }
        media = new Media(songs.get(currentSong).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        currentSong = 0;
        songLabel.setText(songs.get(currentSong).getName());
        speed.getItems().addAll(speeds);
        speed.setValue(100);
        volume.setValue(100);
        volume.valueProperty().addListener((observableValue, number, t1) -> {
            System.out.println(volume.getValue());
        });
        speed.setOnAction(this::changeSpeed);
        volume.setOnMouseDragged(event -> {
            mediaPlayer.setVolume(volume.getValue() / 100.0);
        });
        songBar.setStyle("-fx-accent: #00ff00;");

    }

    public void playMedia() {
          mediaPlayer.play();
          beginTimer();
    }
    public void pauseMedia() {
     mediaPlayer.pause();
        timer.cancel();
        isTimerRunning = false;
    }
    public void resetMedia() {
        mediaPlayer.stop();
        endTimer();
    }
    public void nextMedia() {
        mediaPlayer.stop();
        if (currentSong < songs.size() - 1) {
            currentSong++;
        } else {
            currentSong = 0;
        }
        if (isTimerRunning) {
            endTimer();
        }
        media = new Media(songs.get(currentSong).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(currentSong).getName());
        mediaPlayer.play();

    }
    public void previousMedia() {
        mediaPlayer.stop();
        if (currentSong > 0) {
            currentSong--;
        } else {
            currentSong = songs.size() - 1;
        }
        if (isTimerRunning) {
            endTimer();
        }
        media = new Media(songs.get(currentSong).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(currentSong).getName());
        mediaPlayer.play();

    }
    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(speed.getValue() / 100.0);

    }

    public void beginTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                isTimerRunning = true;
                 double current = mediaPlayer.getCurrentTime().toSeconds();
                    double total = mediaPlayer.getTotalDuration().toSeconds();
                    songBar.setProgress(current / total);
                if (current/total == 1) {
                    endTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

    }
    public void endTimer() {
        timer.cancel();
        timer.purge();
        isTimerRunning = false;

    }
}