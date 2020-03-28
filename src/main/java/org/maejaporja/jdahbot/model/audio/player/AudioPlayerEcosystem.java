package org.maejaporja.jdahbot.model.audio.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.maejaporja.jdahbot.model.audio.player.listener.AudioTrackScheduler;
import org.maejaporja.jdahbot.model.base.BaseEcosystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class AudioPlayerEcosystem extends BaseEcosystem {

    public static AudioPlayerManager audioPlayerManager;
    private AudioLoadResultHandler audioLoadResultHandler;
    private AudioPlayerHandler audioPlayerHandler;
    private AudioTrackScheduler audioEvent;
    private AudioPlayer audioPlayer;

    private static final int DEFAULT_VOLUME = 70;

    static {
        audioPlayerManager = AudioPlayerManagerSingleton.getInstance();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public AudioPlayerEcosystem(){
        this(new HashMap<>());
    }
    public AudioPlayerEcosystem(Map<String, Object> environment){
        super(environment);
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.audioEvent = new AudioTrackScheduler(audioPlayer, environment);
        this.audioPlayerHandler = new AudioPlayerHandler(audioPlayer);
        this.audioLoadResultHandler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                MessageChannel channel = (MessageChannel) getEnvironment().get("messageChannel");
                Integer queueMax = (Integer) getEnvironment().get("queueMax");
                AudioTrackScheduler audioTrackScheduler = audioEvent;
                Queue<AudioTrack> audioTrackQueue = audioTrackScheduler.getQueue();
                String msg = "> Queue is now at maximum capacity("+queueMax+')';
                if(!(audioTrackQueue.size() > queueMax-1)) {
                    String author = audioTrack.getInfo().author;
                    String title = audioTrack.getInfo().title;
                    msg = String.format("> Adding to queue: %s - %s", author, title);
                    audioEvent.queue(audioTrack);
                }
                channel.sendMessage(msg).queue();
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
        this.audioPlayer.setVolume(DEFAULT_VOLUME);
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
    public AudioTrackScheduler getAudioEvent() {
        return audioEvent;
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
    public void setAudioEvent(AudioTrackScheduler audioEvent) {
        this.audioEvent = audioEvent;
    }
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

}