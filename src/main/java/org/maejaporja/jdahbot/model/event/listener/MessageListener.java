package org.maejaporja.jdahbot.model.event.listener;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.TEXT))
        {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getAuthor(), event.getMessage().getContentDisplay());
        }
        else
        {
            System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
        }
    }
}
