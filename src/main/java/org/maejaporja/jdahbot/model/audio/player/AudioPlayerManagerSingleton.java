package org.maejaporja.jdahbot.model.audio.player;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import java.util.Objects;

public class AudioPlayerManagerSingleton {

    private static AudioPlayerManager instance;

    public static AudioPlayerManager getInstance() {
        if(Objects.isNull(instance)){
            instance = new DefaultAudioPlayerManager();
        }
        return instance;
    }

}
