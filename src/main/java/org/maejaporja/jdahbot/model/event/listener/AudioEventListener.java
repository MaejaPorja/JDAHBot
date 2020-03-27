package org.maejaporja.jdahbot.model.event.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerEcosystem;
import org.maejaporja.jdahbot.model.audio.player.AudioPlayerHandler;
import org.maejaporja.jdahbot.model.audio.player.listener.AudioTrackScheduler;
import org.maejaporja.jdahbot.model.ecosystem.EcosystemManager;
import org.maejaporja.jdahbot.model.base.BaseEventListener;
import org.maejaporja.jdahbot.model.event.pattern.EventPattern;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;


public class AudioEventListener extends BaseEventListener {

    private final EcosystemManager<AudioPlayerEcosystem> audioPlayerEcosystemManager;

    public AudioEventListener(){
        super(
                new EventPattern[]{
                        EventPattern.PLAY,
                        EventPattern.LEAVE
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
        EcosystemManager.EcosystemExecutor audioPlayerEcosystemExecutor =
                audioPlayerEcosystemManager.new EcosystemExecutor(audioPlayerEcosystem);
        EventPattern pattern = EventPattern.valueOf(eventPattern);

        if(pattern.equals(EventPattern.PLAY)) {
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
                audioPlayerManager.loadItem(eventMessage, audioLoadResultHandler);
            };
            audioPlayerEcosystemExecutor.execute(connect.andThen(play));
        } else if(pattern.equals(EventPattern.LEAVE)){
            Consumer<AudioPlayerEcosystem> leave = audioPlayerEcosystem1 ->  {
                AudioManager audioManager = guild.getAudioManager();
                audioManager.setSendingHandler(null);
                audioManager.setReceivingHandler(null);
                audioManager.closeAudioConnection();
            };
            audioPlayerEcosystemExecutor.execute(leave);
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
            EventPattern.valueOf(pattern);
            return true;
        } catch(IllegalArgumentException err){
            return false;
        }
    }

    private AudioPlayerEcosystem getAudioEcosystem(Guild guild){
        String guildId = guild.getId();
        AudioPlayerEcosystem audioPlayerEcosystem = audioPlayerEcosystemManager.getEcosystem(guildId);
        if(Objects.isNull(audioPlayerEcosystem)) {
            AudioEventAdapter audioPlayerEvent = new AudioTrackScheduler();
            audioPlayerEcosystem =  new AudioPlayerEcosystem(audioPlayerEvent);
            synchronized(audioPlayerEcosystemManager){
                audioPlayerEcosystemManager.addEcosystem(guildId, audioPlayerEcosystem);
            }
        }
        return audioPlayerEcosystem;
    }

}
