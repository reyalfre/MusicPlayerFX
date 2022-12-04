package com.alfredo.musicplayerfx.controller;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    boolean firstTime = true;
    @FXML
    private ToggleButton playPause;
    @FXML
    private MediaView player;
    @FXML
    private Slider volume;
    @FXML
    private Slider time;
    @FXML
    private ImageView albumArt;
    @FXML
    private Button prevButton, nextButton, browse;
    @FXML
    private Label titleMusic, groupMusic, albumMusic;
    private ArrayList<Media> mediaFiles = new ArrayList();
    private int counter = -1;

    private File file;
    private FileChooser fileChooser;
    private Stage stage;
    private MediaPlayer mediaPlayer;
    private Media media;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            playPause.requestFocus();
    }

    @FXML
    private void playPauseClicked(ActionEvent event) {
        if (firstTime) {
            file = null;
            stage = (Stage) playPause.getScene().getWindow();
            fileChooser = new FileChooser();
            FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Audio files", "*.mp3, *.wav");
            fileChooser.getExtensionFilters().add(fileExtension);
            file = fileChooser.showOpenDialog(stage);
            System.out.println(file);
            if (file != null) {
                media = new Media(file.toURI().toASCIIString());
                mediaFiles.add(media);
                mediaPlayer = new MediaPlayer(media);
                prevButton.setDisable(false);
                nextButton.setDisable(false);
                ++counter;
                player = new MediaView(mediaPlayer);
                mediaPlayer.setAutoPlay(true);
                player.getMediaPlayer().getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
                    if (change.wasAdded()) {
                        if (change.getKey().equals("image")) {
                            Image art = (Image) change.getValueAdded();
                            System.out.println();
                            double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                            albumArt.setX(200);
                            albumArt.setImage(art);
                            albumArt.setX(200);
                        }
                    }
                });
                volume.valueProperty().addListener((Observable observable) -> {
                    if (volume.isValueChanging()) {
                        System.out.println(volume.getValue());
                        player.getMediaPlayer().setVolume(volume.getValue() / 100);
                    }
                });
                /*time.valueProperty().addListener((Observable observable) -> {
                    if (time.isValueChanging()) {
                        System.out.println(time.getValue());
                        player.getMediaPlayer().setAudioSpectrumInterval(time.getValue() / 100);
                    }
                });*/
                time.valueProperty().addListener((p,o,value) -> {
                    if (time.isPressed()) {
                        System.out.println(time.getValue());
                        player.getMediaPlayer().seek(Duration.seconds(value.doubleValue()));
                        // player.getMediaPlayer().setAudioSpectrumInterval(time.getValue() / 100);
                    }
                });
                player.getMediaPlayer().setOnEndOfMedia(() -> {
                    playPause.setSelected(false);
                });
                firstTime = false;

            } else {
                playPause.setSelected(false);
            }
        } else {
            System.out.println("I'm here");
            if (playPause.isSelected()) {
                player.getMediaPlayer().play();
            } else {
                player.getMediaPlayer().pause();
            }
        }
    }


    @FXML
    private void browseClicked(ActionEvent event) {
        firstTime = false;
        stage = (Stage) playPause.getScene().getWindow();
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Audio files", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(fileExtension);
        file = fileChooser.showOpenDialog(stage);
        media = new Media(file.toURI().toASCIIString());
        mediaFiles.add(media);
        mediaPlayer = new MediaPlayer(media);
        try {
            player.getMediaPlayer().dispose();
        } catch (Exception e) {

        }
        prevButton.setDisable(false);
        nextButton.setDisable(false);
        playPause.setSelected(true);
        ++counter;
        mediaPlayer.setAutoPlay(true);
        player = new MediaView(mediaPlayer);
        player.getMediaPlayer().getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
            if (change.wasAdded()) {
                if (change.getKey().equals("image")) {
                    Image art = (Image) change.getValueAdded();
                    System.out.println();
                    double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                    albumArt.setX(50);
                    albumArt.setImage(art);
                    albumArt.setX(50);
                }
            }
        });
        volume.valueProperty().addListener((Observable observable) -> {
            if (volume.isValueChanging()) {
                System.out.println(volume.getValue());
                player.getMediaPlayer().setVolume(volume.getValue() / 100);
            }
        });
        time.valueProperty().addListener((p,o,value) -> {
            if (time.isPressed()) {
                System.out.println(time.getValue());
                player.getMediaPlayer().seek(Duration.seconds(value.doubleValue()*mediaPlayer.getStopTime().toSeconds()));
               // player.getMediaPlayer().setAudioSpectrumInterval(time.getValue() / 100);
            }
        });
        player.getMediaPlayer().setOnEndOfMedia(() -> {
            playPause.setSelected(false);
        });
        mediaPlayer.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
            time.setValue(mediaPlayer.getCurrentTime().toMillis()/mediaPlayer.getStopTime().toMillis());
        }));
    }

    @FXML
    private void prevClicked(ActionEvent event) {
        if (counter == 0) {
            player.getMediaPlayer().seek(Duration.ZERO);
        } else {
            player.getMediaPlayer().dispose();
            albumArt.setImage(null);
            player = new MediaView(new MediaPlayer(mediaFiles.get(--counter)));
            player.getMediaPlayer().play();
            playPause.setSelected(true);
            player.getMediaPlayer().setOnEndOfMedia(() -> {
                playPause.setSelected(false);
            });
            player.getMediaPlayer().getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
                if (change.wasAdded()) {
                    handleMetadata(change.getKey(), change.getValueAdded());
                    if (change.getKey().equals("image")) {
                        Image art = (Image) change.getValueAdded();
                        System.out.println("I'm in");
                        double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                        albumArt.setX(50);
                        albumArt.setImage(art);
                        albumArt.setX(50);
                    }
                    if (change.getKey().equals("artist")){
                        Label nombre = (Label) change.getValueAdded();
                        System.out.println(nombre);

                    }
                }
            });
        }
    }

    private void handleMetadata(String key, Object valueAdded) {
        if (key.equals("title")){
            titleMusic.setText(valueAdded.toString());
        }
    }

    @FXML
    private void nextClicked(ActionEvent event) {
        if (counter + 1 == mediaFiles.size()) {
            player.getMediaPlayer().stop();
            playPause.setSelected(false);
        } else {
            player.getMediaPlayer().dispose();
            albumArt.setImage(null);
            player = new MediaView(new MediaPlayer(mediaFiles.get(++counter)));
            playPause.setSelected(true);
            player.getMediaPlayer().play();
            player.getMediaPlayer().setOnEndOfMedia(() -> {
                playPause.setSelected(false);
            });
            player.getMediaPlayer().getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
                if (change.wasAdded()) {
                    if (change.getKey().equals("image")) {
                        Image art = (Image) change.getValueAdded();
                        System.out.println();
                        double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                        albumArt.setX(50);
                        albumArt.setImage(art);
                        albumArt.setX(50);
                    }
                }
            });
        }

    }
}