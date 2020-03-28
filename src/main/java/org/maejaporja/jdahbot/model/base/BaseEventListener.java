package org.maejaporja.jdahbot.model.base;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.maejaporja.jdahbot.model.event.pattern.AudioEventPattern;
import org.maejaporja.jdahbot.utils.ApplicationConfig;

import java.util.Iterator;

abstract public class BaseEventListener extends ListenerAdapter
        implements Iterable<AudioEventPattern> {

    private final AudioEventPattern[] PATTERN;

    protected BaseEventListener(AudioEventPattern[] pattern){
        this.PATTERN = pattern;
    }

    protected boolean checkMessageFormat(String rootMessages, String... messages){ return false; }
    protected boolean checkEventPattern(String pattern){ return false; }
    protected boolean startsWithRootPrefix(String message){
        return message.startsWith(ApplicationConfig.PREFIX);
    }
    protected boolean clearEventMessage(){ return false; }

    public AudioEventPattern[] getPATTERN(){
        return PATTERN;
    }

    @Override
    public Iterator<AudioEventPattern> iterator(){
        return new Iterator<AudioEventPattern>(){
            int index = -1;
            @Override
            public boolean hasNext() {
                return ++index < PATTERN.length;
            }
            @Override
            public AudioEventPattern next() {
                return PATTERN[index];
            }
        };
    }

}
