package org.maejaporja.jdahbot.model.base;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
    protected boolean startsWithRootPrefix(String message){
        return message.startsWith(ApplicationConfig.PREFIX);
    }
    protected boolean clearEventMessage(){ return false; }

    public EventPattern[] getPATTERN(){
        return PATTERN;
    }

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
