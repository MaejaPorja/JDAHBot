package org.maejaporja.jdahbot.model.base;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.maejaporja.jdahbot.model.event.pattern.EventPattern;
import org.maejaporja.jdahbot.utils.ApplicationConfig;

import java.util.Iterator;

abstract public class BaseEventListener extends ListenerAdapter
        implements Iterable<EventPattern> {

    private final EventPattern[] PATTERN;

    protected BaseEventListener(EventPattern[] pattern){
        this.PATTERN = pattern;
    }
    protected boolean checkMessageFormat(String rootMessages, String... messages){ return false; }
    protected boolean checkEventPattern(String pattern){ return false; }
    protected boolean checkEventChannel(Event event){ return false; }
    protected boolean startsWithRootPrefix(String message){
        String[] PREFIXES = ApplicationConfig.PREFIXES;
        String trimMessage = message.trim();
        for(String prefix : PREFIXES){
            if(trimMessage.startsWith(prefix))
                return true;
        } return false;
    }
    protected boolean clearEventMessage(){ return false; }

    public EventPattern[] getPATTERN(){
        return PATTERN;
    }

    @NotNull
    @Override
    public Iterator<EventPattern> iterator(){
        return new Iterator<EventPattern>(){
            int index = -1;
            @Override
            public boolean hasNext() {
                return ++index < PATTERN.length;
            }
            @Override
            public EventPattern next() {
                return PATTERN[index];
            }
        };
    }

}
