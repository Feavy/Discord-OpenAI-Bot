package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;

import java.util.ArrayList;
import java.util.List;

import fr.feavy.simpleopenai.Conversation;

public class DiscordConversation extends Conversation {
    private final List<Message> messages = new ArrayList<>();
    private final List<DiscordChatMessage> conversation = new ArrayList<>();

    public DiscordConversation() {
    }

    public boolean hasMessage(Message referencedMessage) {
        return messages.stream().anyMatch(m -> m.getId().equals(referencedMessage.getId()));
    }

    public void addMessage(Message message) {
        messages.add(message);
        conversation.add(DiscordChatMessage.from(message));
    }

    public void addMessage(int index, Message message) {
        messages.add(index, message);
        conversation.add(index, DiscordChatMessage.from(message));
    }

    public void setLastMessageAfter(Message referencedMessage, Message message) {
        int index = messages.indexOf(referencedMessage);
        messages.add(index + 1, message);
        conversation.add(index + 1, DiscordChatMessage.from(message));
        // removes all messages after the new one
        messages.subList(index + 2, messages.size()).clear();
        conversation.subList(index + 2, conversation.size()).clear();
    }

    public static DiscordConversation fromMessage(Message message) {
        System.out.println("Creating conversation from message");
        DiscordConversation conv = new DiscordConversation();
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
}
