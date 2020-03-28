package org.maejaporja.jdahbot.model.base;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

abstract public class BaseAudioListener extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;

    protected BaseAudioListener(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
    }

    protected AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

}
