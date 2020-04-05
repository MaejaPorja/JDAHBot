package org.maejaporja.jdahbot.model.event.pattern;

import org.maejaporja.jdahbot.utils.ApplicationConfig;

public enum RootEventPattern implements EventPattern {

    Honorable(ApplicationConfig.PREFIXES[0]),
    H(ApplicationConfig.PREFIXES[1]);

    private String pattern;

    private RootEventPattern(String pattern){
        this.pattern = pattern;
    }

    public String getPattern(){
        return this.pattern;
    }

}
