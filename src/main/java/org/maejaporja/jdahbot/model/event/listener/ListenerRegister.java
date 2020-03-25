package org.maejaporja.jdahbot.model.event.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

public class ListenerRegister {
    private JDA jda;
    private JDABuilder jdaBuilder;

    public ListenerRegister(@Nonnull JDA jda){
        this.jda = jda;
    }
    public ListenerRegister(@Nonnull JDABuilder jdaBuilder){
        this.jdaBuilder = jdaBuilder;
    }

    public void register(@Nonnull Collection<EventListener> eventListeners){
        if(Objects.nonNull(jda)) jda.addEventListener(eventListeners.toArray());
        else jdaBuilder.addEventListeners(eventListeners.toArray());
    }
}
