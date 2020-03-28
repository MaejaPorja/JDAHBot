package org.maejaporja.jdahbot.model.audio.player.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.maejaporja.jdahbot.model.base.BaseAudioListener;
import org.maejaporja.jdahbot.utils.TimeFormat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AudioTrackScheduler extends BaseAudioListener
    implements Iterable<AudioTrack> {

    private boolean repeating;
    private final Queue<AudioTrack> queue;
    private AudioTrack lastTrack;
    private Map<String, Object> data;

    public static final int DEFAULT_QUEUE_MAX = 16;

    public AudioTrackScheduler(AudioPlayer audioPlayer){
        this(audioPlayer, null);
    }
    public AudioTrackScheduler(AudioPlayer audioPlayer, Map<String, Object> data){
        super(audioPlayer);
        this.queue = new LinkedList<>();
        this.data = data;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        MessageChannel channel = (MessageChannel) data.get("messageChannel");
        AudioTrackInfo audioTrackInfo = track.getInfo();
        String author = audioTrackInfo.author;
        String title = audioTrackInfo.title;
        long length = audioTrackInfo.length;
        String message = String.format("> Now playing: %s — %s — %s%n", author, title, TimeFormat.getTimestamp(length));
        channel.sendMessage(message).queue();
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
        Integer queueMax = (Integer) data.get("queueMax");
        AudioPlayer audioPlayer = getAudioPlayer();
        if(!(queue.size() > queueMax-1) && !audioPlayer.startTrack(audioTrack, true)){
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

    public Queue<AudioTrack> getQueue(){
        return this.queue;
    }

    @NotNull
    @Override
    public Iterator<AudioTrack> iterator() {
        return queue.iterator();
    }
}
