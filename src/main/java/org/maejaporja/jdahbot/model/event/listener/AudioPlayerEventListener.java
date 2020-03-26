package org.maejaporja.jdahbot.model.event.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerHandler;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerManagerSingleton;
import org.maejaporja.jdahbot.model.audio.player.listener.TrackScheduler;
import org.maejaporja.jdahbot.model.base.BaseEventListener;

import javax.annotation.Nonnull;
import java.util.function.Consumer;


public class AudioPlayerEventListener extends BaseEventListener {

    public AudioPlayerEventListener(){
        super("play");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Message message = event.getMessage();
        User author = message.getAuthor();
        Member member = message.getMember();
        VoiceChannel channel = member.getVoiceState().getChannel();

        String[] splitMessage = message.getContentRaw().split(" ", 3);
        if(author.isBot() || !checkCommandFormat(splitMessage)) return;
        else if(splitMessage.length>2){
            connectAndPlayOn(channel, splitMessage[2]);
        }
        else {
            System.out.println("Display manual");
        }
    }

    @Override
    protected boolean checkCommandFormat(String[] messages){
        return messages.length > 1 && startsWithRootPrefix(messages[0])
                && messages[1].equalsIgnoreCase(PATTERN);
    }

    private void connectAndPlayOn(VoiceChannel channel, String identifier){
        Node<VoiceChannel> connectTo = new Node<>((AudioPlayerEcosystem audioPlayerEcosystem) -> {
            Guild guild = channel.getGuild();
            AudioPlayerHandler audioPlayerHandler = audioPlayerEcosystem.audioPlayerHandler;
            AudioManager audioManager = guild.getAudioManager();
            audioManager.setSendingHandler(audioPlayerHandler);
            audioManager.setReceivingHandler(audioPlayerHandler);
            audioManager.openAudioConnection(channel);
        }, channel);
        Node<String> play = new Node<>((AudioPlayerEcosystem audioPlayerEcosystem) -> {
            AudioLoadResultHandler audioLoadResultHandler = audioPlayerEcosystem.audioLoadResultHandler;
            AudioPlayerEcosystem.audioPlayerManager.loadItem(identifier, audioLoadResultHandler);
        }, identifier);

        AudioPlayerEcosystem audioPlayerEcosystem = new AudioPlayerEcosystem(connectTo, play);
        audioPlayerEcosystem.connectAndPlay();
    }

    private static class Node<T> {
        Consumer<AudioPlayerEcosystem> item;
        T arg;
        private Node(Consumer<AudioPlayerEcosystem> target, T arg){
            this.item = target;
            this.arg = arg;
        }
    }

    private static class AudioPlayerEcosystem {

        private static AudioPlayerManager audioPlayerManager;
        private AudioLoadResultHandler audioLoadResultHandler;
        private AudioPlayerHandler audioPlayerHandler;
        private AudioPlayer audioPlayer;

        private Node<VoiceChannel> connectTo;
        private Node<String> play;

        static {
            audioPlayerManager = AudioPlayerManagerSingleton.getInstance();
            AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        }

        private AudioPlayerEcosystem(Node<VoiceChannel> connectTo, Node<String> play){
            this.audioPlayer = audioPlayerManager.createPlayer();
            this.audioPlayerHandler = new AudioPlayerHandler(audioPlayer);
            this.audioLoadResultHandler = new AudioLoadResultHandler(){
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    System.out.printf("AudioTrack %s loaded%n", audioTrack.getIdentifier());
                    audioPlayer.playTrack(audioTrack);
                }
                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {

                }
                @Override
                public void noMatches() {
                    System.out.printf("No matches for %s%n", play.arg);
                }
                @Override
                public void loadFailed(FriendlyException e) {
                    System.out.println(e.getMessage());
                }
            };
            audioPlayer.addListener(new TrackScheduler());
            this.connectTo = connectTo;
            this.play = play;
        }

        private void connectAndPlay(){
            connect();
            play();
        }
        private void connect(){
            connectTo.item.accept(this);
        }
        private void play(){
            play.item.accept(this);
        }

    }

}
