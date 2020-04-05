package org.maejaporja.jdahbot.driver;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import org.maejaporja.jdahbot.model.event.listener.AudioEventListener;
import org.maejaporja.jdahbot.model.event.listener.ListenerRegister;
import org.maejaporja.jdahbot.model.event.listener.MessageEventListener;
import org.maejaporja.jdahbot.model.event.listener.ReadyListener;
import org.maejaporja.jdahbot.utils.ApplicationConfig;

import javax.security.auth.login.LoginException;
import java.util.*;

public class JDAHBotDriver {
    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = System.getenv("token");
        token = !Objects.isNull(token) ? token : args[0];
        JDA jdah = ready(getJDABot(token));
    }

    private static JDA ready(JDA jda) throws InterruptedException {
        return jda.awaitReady();
    }
    private static JDA getJDABot(String token) throws LoginException {
        return botBuilder(token).build();
    }
    private static JDABuilder botBuilder(String token) {
        return registerEvent(JDABuilder.createDefault(token));
    }
    private static JDABuilder registerEvent(JDABuilder jdaBuilder){
        Collection<EventListener> eventListeners = new ArrayList<>();
        eventListeners.add(new ReadyListener(ApplicationConfig.APPLICATION));
        eventListeners.add(new AudioEventListener());
        eventListeners.add(new MessageEventListener());

        ListenerRegister jdaLR = new ListenerRegister(jdaBuilder);
        jdaLR.register(eventListeners);

        return jdaBuilder;
    }

}
