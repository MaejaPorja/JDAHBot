package org.maejaporja.jdahbot.model.base;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.maejaporja.jdahbot.utils.ApplicationConfig;

import javax.annotation.Nonnull;
import java.util.Map;

abstract public class BaseListener extends ListenerAdapter {

    protected final String PATTERN;

    protected BaseListener(String pattern){
        this.PATTERN = pattern;
    }

    protected boolean checkCommandFormat(String[] messages){ return false; }
    protected boolean startsWithRootPrefix(String message){
        return message.startsWith(ApplicationConfig.PREFIX);
    }
    protected <E extends GenericEvent> Map<String,Object> extractEventData(@Nonnull E event){
        return null;
    }

}
