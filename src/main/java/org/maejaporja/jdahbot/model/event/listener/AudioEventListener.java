package org.maejaporja.jdahbot.model.event.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerEcosystem;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerHandler;
import org.maejaporja.jdahbot.model.audio.player.listener.AudioTrackScheduler;
import org.maejaporja.jdahbot.model.ecosystem.EcosystemManager;
import org.maejaporja.jdahbot.model.base.BaseEventListener;
import org.maejaporja.jdahbot.model.event.pattern.AudioEventPattern;
import org.maejaporja.jdahbot.utils.TimeFormat;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class AudioEventListener extends BaseEventListener {

    private final EcosystemManager<AudioPlayerEcosystem> audioPlayerEcosystemManager;

    public AudioEventListener(){
        super(
                new AudioEventPattern[]{
                        AudioEventPattern.PLAY,
                        AudioEventPattern.LEAVE,
                        AudioEventPattern.SKIP,
                        AudioEventPattern.VOLUME,
                        AudioEventPattern.QUEUE
                }
        );
        this.audioPlayerEcosystemManager = new EcosystemManager<>();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Message message = event.getMessage();
        User author = message.getAuthor();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        VoiceChannel channel = member.getVoiceState().getChannel();

        String[] splitMessage = message.getContentRaw().split(" ", 3);

        String rootMessage = splitMessage[0];
        String eventPattern = splitMessage.length > 1 ? splitMessage[1].toUpperCase() : "";
        String eventMessage = splitMessage.length > 2 ? splitMessage[2] : "";

        if(author.isBot() || !checkMessageFormat(rootMessage, eventPattern) || Objects.isNull(channel)) {
            return;
        }

        AudioPlayerEcosystem audioPlayerEcosystem = getAudioEcosystem(guild);
        Map<String, Object> audioPlayerEcosystemEnvironment = audioPlayerEcosystem.getEnvironment();
        audioPlayerEcosystemEnvironment.put("messageChannel", message.getChannel());
        audioPlayerEcosystemEnvironment.put("queueMax", AudioTrackScheduler.DEFAULT_QUEUE_MAX);
        setAudioEcosystemEnvironment(audioPlayerEcosystem, audioPlayerEcosystemEnvironment);

        EcosystemManager.EcosystemExecutor audioPlayerEcosystemExecutor =
                audioPlayerEcosystemManager.new EcosystemExecutor(audioPlayerEcosystem);
        AudioEventPattern pattern = AudioEventPattern.valueOf(eventPattern);

        if(pattern.equals(AudioEventPattern.PLAY)) {
            Consumer<AudioPlayerEcosystem> connect = audioPlayerEcosystem1 -> {
                AudioPlayerHandler audioPlayerHandler = audioPlayerEcosystem1.getAudioPlayerHandler();
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSendingHandler(audioPlayerHandler);
                audioManager.setReceivingHandler(audioPlayerHandler);
                audioManager.openAudioConnection(channel);
            };
            Consumer<AudioPlayerEcosystem> play = audioPlayerEcosystem1 -> {
                AudioPlayerManager audioPlayerManager = AudioPlayerEcosystem.audioPlayerManager;
                AudioLoadResultHandler audioLoadResultHandler = audioPlayerEcosystem1.getAudioLoadResultHandler();
                audioPlayerManager.loadItemOrdered(audioPlayerEcosystem, eventMessage, audioLoadResultHandler);
            };
            audioPlayerEcosystemExecutor.execute(connect.andThen(play));
        } else if(pattern.equals(AudioEventPattern.LEAVE)){
            Consumer<AudioPlayerEcosystem> leave = audioPlayerEcosystem1 -> {
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSendingHandler(null);
                audioManager.setReceivingHandler(null);
                audioManager.closeAudioConnection();
            };
            audioPlayerEcosystemExecutor.execute(leave);
        } else if(pattern.equals(AudioEventPattern.SKIP)){
            Consumer<AudioPlayerEcosystem> skip = audioPlayerEcosystem1 -> {
                AudioTrackScheduler audioTrackScheduler = audioPlayerEcosystem1.getAudioEvent();
                audioTrackScheduler.nextTrack();
            };
            audioPlayerEcosystemExecutor.execute(skip);
        } else if(pattern.equals(AudioEventPattern.QUEUE)){
            Consumer<AudioPlayerEcosystem> queue = audioPlayerEcosystem1 -> {
                StringBuilder stringBuilder = new StringBuilder("```");
                Iterator<AudioTrack> audioTrackIterator = audioPlayerEcosystem1.getAudioEvent().iterator();
                int counter = 1;
                while(audioTrackIterator.hasNext()){
                    AudioTrack audioTrack = audioTrackIterator.next();
                    AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
                    String audioTrackAuthor = audioTrackInfo.author;
                    String audioTrackTitle = audioTrackInfo.title;
                    long length = audioTrackInfo.length;
                    stringBuilder.append(
                            String.format(
                                    "%d) %s — %s — %s%n", counter++, audioTrackAuthor, audioTrackTitle,
                                    TimeFormat.getTimestamp(length)
                            )
                    );
                }
                stringBuilder.append("```");
                message.getChannel().sendMessage(stringBuilder).queue();
            };
            audioPlayerEcosystemExecutor.execute(queue);
        } else if(pattern.equals(AudioEventPattern.VOLUME)){
            Consumer<AudioPlayerEcosystem> volume = audioPlayerEcosystem1 -> {
                AudioPlayer audioPlayer = audioPlayerEcosystem1.getAudioPlayer();
                int audioPlayerVolume = audioPlayer.getVolume();
                String mesg = "> Current player volume: "+audioPlayerVolume;
                try{
                    if(!eventMessage.equals("")){
                        audioPlayerVolume = Integer.parseInt(eventMessage);
                        audioPlayerVolume = audioPlayerVolume > 150 ? 150 :
                                audioPlayerVolume < 0 ? 0 : audioPlayerVolume;
                        audioPlayer.setVolume(audioPlayerVolume);
                        mesg = "> Current player volume: "+audioPlayerVolume;
                    }
                } catch(NumberFormatException err){
                    mesg = "> Volume must be integer.";
                }
                message.getChannel().sendMessage(mesg).queue();
            };
            audioPlayerEcosystemExecutor.execute(volume);
        }
    }
    @Override
    protected boolean checkMessageFormat(String rootMessage, String... messages){
        String eventPattern = messages[0];
        return startsWithRootPrefix(rootMessage) && checkEventPattern(eventPattern);
    }
    @Override
    protected boolean checkEventPattern(String pattern){
        try {
            AudioEventPattern.valueOf(pattern);
            return true;
        } catch(IllegalArgumentException err){
            return false;
        }
    }

    private AudioPlayerEcosystem getAudioEcosystem(Guild guild){
        String guildId = guild.getId();
        AudioPlayerEcosystem audioPlayerEcosystem = audioPlayerEcosystemManager.getEcosystem(guildId);
        if(Objects.isNull(audioPlayerEcosystem)) {
            audioPlayerEcosystem =  new AudioPlayerEcosystem();
            synchronized(audioPlayerEcosystemManager){
                audioPlayerEcosystemManager.addEcosystem(guildId, audioPlayerEcosystem);
            }
        }
        return audioPlayerEcosystem;
    }
    private void setAudioEcosystemEnvironment(AudioPlayerEcosystem audioPlayerEcosystem
            , Map<String, Object> environment){
        audioPlayerEcosystem.setEnvironment(environment);
    }

}
