package org.maejaporja.jdahbot.model.event.listener;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

public class ReadyListener implements EventListener {

    private String target;

    public ReadyListener() {
        this("Event");
    }
    public ReadyListener(String target){
        this.target = target;
    }

    public void onEvent(@Nonnull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.printf("%s is ready!", target);
        }
    }
}
