package org.maejaporja.jdahbot.model.event.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.maejaporja.jdahbot.model.base.BaseListener;

import javax.annotation.Nonnull;


public class AudioPlayerListener extends BaseListener {

    public AudioPlayerListener(){
        super("play");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Message message = event.getMessage();
        User author = message.getAuthor();

        String[] splitMessage = message.getContentRaw().split(" ", 3);
        if(author.isBot() || !checkCommandFormat(splitMessage)) return;
        else if(splitMessage.length>2){
            Guild guild = event.getGuild();
            VoiceChannel channel = guild.getVoiceChannelsByName("General", false).get(0);
            AudioManager manager = guild.getAudioManager();
            System.out.println(splitMessage[2]);
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
}
