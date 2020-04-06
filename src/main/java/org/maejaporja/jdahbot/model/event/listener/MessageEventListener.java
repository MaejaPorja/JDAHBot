package org.maejaporja.jdahbot.model.event.listener;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.maejaporja.jdahbot.model.base.BaseEventListener;
import org.maejaporja.jdahbot.model.event.pattern.MessageEventPattern;
import org.maejaporja.jdahbot.utils.ApplicationConfig;
import org.maejaporja.jdahbot.utils.Util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MessageEventListener extends BaseEventListener {


    public MessageEventListener() {
        super(new MessageEventPattern[]{
                MessageEventPattern.SAY,
                MessageEventPattern.HELLO,
                MessageEventPattern.เรียกอาท,
                MessageEventPattern.จริง,
        });
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = message.getAuthor();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        String[] extractedMessage = Util.CommandUtil.extractMessage(message.getContentRaw(), " ", 3);

        String rootMessage = extractedMessage[0].toUpperCase();
        String eventPattern = extractedMessage.length > 1 ? extractedMessage[1].toUpperCase() : "";
        String eventMessage = extractedMessage.length > 2 ? extractedMessage[2] : "";

        if (author.isBot() || !checkEventChannel(event) || !checkMessageFormat(rootMessage, eventPattern)) return;

        MessageEventPattern pattern = MessageEventPattern.valueOf(
                Util.CommandUtil.isSingleLengthCommand(extractedMessage) ? rootMessage : eventPattern
        );

        if (pattern.equals(MessageEventPattern.SAY)) {
            message.delete().queue(e ->
                message.getChannel().sendMessage(eventMessage).queue()
            );
        } else if (pattern.equals(MessageEventPattern.HELLO)) {
            message.getChannel().sendMessage("Test Test").queue();
        } else if (pattern.equals(MessageEventPattern.จริง)) {
            String[] jings = ApplicationConfig.JINGS;
            String jing = jings[(int) (Math.random() * jings.length)];
            message.getChannel().sendMessage(jing).queue();
        }
//        } else if (pattern.equals(MessageEventPattern.เรียกอาท)) {
//            message.getChannel().sendMessage(
//                    String.format("ไอห่า สบายดีไหมนิ <@%s>", ApplicationConfig.MUZASHII_ID)
//            ).queue();
//        }
    }

    @Override
    protected boolean checkMessageFormat(String rootMessage, String... messages){
        String eventPattern = messages[0];
        if (eventPattern.isEmpty()) return checkEventPattern(rootMessage);
        return startsWithRootPrefix(rootMessage) && checkEventPattern(eventPattern);
    }
    @Override
    protected boolean checkEventPattern(String pattern){
        try {
            MessageEventPattern.valueOf(pattern);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    @Override
    protected boolean checkEventChannel(Event event){
        return ((MessageReceivedEvent) event).isFromType(ChannelType.TEXT);
    }

}
