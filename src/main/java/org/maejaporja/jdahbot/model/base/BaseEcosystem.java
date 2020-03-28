package org.maejaporja.jdahbot.model.base;

import java.util.HashMap;
import java.util.Map;

abstract public class BaseEcosystem {

    private Map<String, Object> environment;

    protected BaseEcosystem(){
        this(new HashMap<>());
    }
    protected BaseEcosystem(Map<String, Object> environment){
        this.environment = environment;
    }

    public Map<String, Object> getEnvironment(){
        return environment;
    }
    public void setEnvironment(Map<String, Object> environment){
        this.environment = environment;
    }

}
