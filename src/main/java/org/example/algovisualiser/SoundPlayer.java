package org.example.algovisualiser;

import javafx.scene.media.AudioClip;
import javafx.application.Platform;

public class SoundPlayer {
    private AudioClip audioClip;
    private double volume = 0.2;
    private int _i = 0;

    public SoundPlayer(String soundFilePath) {
        audioClip = new AudioClip(getClass().getResource(soundFilePath).toString());
        audioClip.setVolume(volume);
    }

    public void setVolume(double volume) {
        this.volume = volume;
        audioClip.setVolume(volume);
    }

    public void playSound(int number, int maxValue, int speed) {
        final double[] pitch = new double[1];

        pitch[0] = 1.0 + ((double) number / maxValue);
        if (pitch[0] > 2.0) pitch[0] = 1.7;

        final double finalPitch = pitch[0];

        _i++;
        if (_i % 3 == 0) {
            Thread soundThread = new Thread(() -> {
                Platform.runLater(() -> {
                    audioClip.setRate(finalPitch);
                    audioClip.play();
                });
            });
            soundThread.setDaemon(true);
            soundThread.start();
            _i = 0;
        }
    }
}
