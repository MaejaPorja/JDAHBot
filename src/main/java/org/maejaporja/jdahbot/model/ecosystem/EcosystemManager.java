package org.maejaporja.jdahbot.model.ecosystem;


import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class EcosystemManager<T> extends HashMap<String,T> {

    public void addEcosystem(String key, T value){
        put(key, value);
    }
    public T getEcosystem(String key){
        return get(key);
    }

    public class EcosystemExecutor {
        private T ecosystem;

        public EcosystemExecutor(T ecosystem){
            this.ecosystem = ecosystem;
        }

        public void execute(Consumer<T> consumer){
            consumer.accept(ecosystem);
        }
        public <R> R execute(Function<T,R> function){
            return function.apply(ecosystem);
        }
    }

}
