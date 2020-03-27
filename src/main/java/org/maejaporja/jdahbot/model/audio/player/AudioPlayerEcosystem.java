package org.maejaporja.jdahbot.model.audio.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Objects;

public class AudioPlayerEcosystem {

    public static AudioPlayerManager audioPlayerManager;
    private AudioLoadResultHandler audioLoadResultHandler;
    private AudioPlayerHandler audioPlayerHandler;
    private AudioPlayer audioPlayer;

    static {
        audioPlayerManager = AudioPlayerManagerSingleton.getInstance();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public AudioPlayerEcosystem(AudioEventAdapter audioEvent){
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.audioPlayerHandler = new AudioPlayerHandler(audioPlayer);
        this.audioLoadResultHandler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                String msg = "Adding to queue: " + audioTrack.getInfo().title;
                if (Objects.isNull(audioPlayer.getPlayingTrack()))
                    msg += "\nand the Player has started playing;";
                audioPlayer.playTrack(audioTrack);
//                mng.scheduler.queue(track);
//                channel.sendMessage(msg).queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

            }
            @Override
            public void noMatches() {

            }
            @Override
            public void loadFailed(FriendlyException e) {

            }
        };
        this.audioPlayer.addListener(audioEvent);
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }
    public AudioLoadResultHandler getAudioLoadResultHandler() {
        return audioLoadResultHandler;
    }
    public AudioPlayerHandler getAudioPlayerHandler() {
        return audioPlayerHandler;
    }
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public static void setAudioPlayerManager(AudioPlayerManager audioPlayerManager) {
        AudioPlayerEcosystem.audioPlayerManager = audioPlayerManager;
    }
    public void setAudioLoadResultHandler(AudioLoadResultHandler audioLoadResultHandler) {
        this.audioLoadResultHandler = audioLoadResultHandler;
    }
    public void setAudioPlayerHandler(AudioPlayerHandler audioPlayerHandler) {
        this.audioPlayerHandler = audioPlayerHandler;
    }
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

}