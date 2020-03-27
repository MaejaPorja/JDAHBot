package org.maejaporja.jdahbot.model.base;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

abstract public class BaseAudioListener extends AudioEventAdapter {

    private AudioPlayer audioPlayer;

    protected BaseAudioListener(){}
    protected BaseAudioListener(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

}
