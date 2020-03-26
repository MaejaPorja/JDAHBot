package org.maejaporja.jdahbot.model.audio.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Objects;

public class AudioPlayerHandler implements AudioSendHandler, AudioReceiveHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerHandler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return Objects.nonNull(lastFrame);
    }

    @Override
    @Nullable
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

}
