package org.maejaporja.jdahbot.driver;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.maejaporja.jdahbot.model.event.ReadyListener;

import javax.security.auth.login.LoginException;

public class JDAHBotDriver {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(args[0])
                .addEventListeners(new ReadyListener()).build();
    }
}
