package org.maejaporja.jdahbot.model.audio.player.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.maejaporja.jdahbot.model.base.BaseAudioListener;
import org.maejaporja.jdahbot.utils.TimeFormat;

import java.util.LinkedList;
import java.util.Queue;

public class AudioTrackScheduler extends BaseAudioListener {

    private boolean repeating;
    private final Queue<AudioTrack> queue;
    private AudioTrack lastTrack;

    public AudioTrackScheduler(AudioPlayer audioPlayer){
        super(audioPlayer);
        this.queue = new LinkedList<>();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo audioTrackInfo = track.getInfo();
        String author = audioTrackInfo.author;
        String title = audioTrackInfo.title;
        long length = audioTrackInfo.length;
        System.out.printf("Now playing: %s - %s : %s%n", author, title, TimeFormat.getTimestamp(length));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack audioTrack, AudioTrackEndReason endReason) {
        AudioPlayer audioPlayer = getAudioPlayer();
        this.lastTrack = audioTrack;
        if(endReason.mayStartNext) {
            if(repeating)
                audioPlayer.startTrack(lastTrack.makeClone(), false);
            else
                nextTrack();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    public void queue(AudioTrack audioTrack){
        AudioPlayer audioPlayer = getAudioPlayer();
        if(!audioPlayer.startTrack(audioTrack, true)){
            queue.offer(audioTrack);
        }
    }
    public void nextTrack(){
        AudioPlayer audioPlayer = getAudioPlayer();
        audioPlayer.startTrack(queue.poll(), false);
    }
    public boolean isRepeating(){
        return repeating;
    }
    public void setRepeating(boolean repeating){
        this.repeating = repeating;
    }

}
