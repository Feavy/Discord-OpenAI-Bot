package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Conversation {
    private final List<Message> messages = new ArrayList<>();
    private final List<String> messagesStr = new ArrayList<>();

    public Conversation() {
    }

    public boolean hasMessage(Message referencedMessage) {
        return messages.stream().anyMatch(m -> m.getId().equals(referencedMessage.getId()));
    }

    public void addMessage(Message message) {
        messages.add(message);
        messagesStr.add(format(message.getContentRaw()));
    }

    public void addMessage(int index, Message message) {
        messages.add(index, message);
        messagesStr.add(index, format(message.getContentRaw()));
    }

    public void setLastMessageAfter(Message referencedMessage, Message message) {
        int index = messages.indexOf(referencedMessage);
        messages.add(index + 1, message);
        messagesStr.add(index + 1, format(message.getContentRaw()));
        // removes all messages after the new one
        messages.subList(index + 2, messages.size()).clear();
        messagesStr.subList(index + 2, messagesStr.size()).clear();
    }

    public String text() {
        return String.join("\n\n", messagesStr) + "\n\n";
    }

    @Override
    public String toString() {
        return text();
    }

    public static Conversation fromMessage(Message message) {
        System.out.println("Creating conversation from message");
        Conversation conv = new Conversation();
        MessageReference reference;
        do {
            conv.addMessage(0, message);
            reference = message.getMessageReference();
            if(reference == null) break;
            if(reference.getMessage() != null) {
                message = reference.getMessage();
            }else{
                // TODO : Optimize to prevent blocking
                message = reference.resolve().complete();
            }
        } while (message != null);
        return conv;
    }

    private static String format(String contentRaw) {
        if (contentRaw.endsWith("??")) {
            contentRaw = contentRaw.substring(0, contentRaw.length() - 1);
        } else if (contentRaw.startsWith("!ai")) {
            contentRaw = contentRaw.substring(3);
        }
        return contentRaw;
    }
}
