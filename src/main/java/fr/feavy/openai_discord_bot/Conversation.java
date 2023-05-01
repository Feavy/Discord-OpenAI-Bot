package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Conversation {
    private final List<Message> messages = new ArrayList<>();
    private final List<ChatMessage> conversation = new ArrayList<>();

    public Conversation() {
    }

    public boolean hasMessage(Message referencedMessage) {
        return messages.stream().anyMatch(m -> m.getId().equals(referencedMessage.getId()));
    }

    public void addMessage(Message message) {
        messages.add(message);
        conversation.add(ChatMessage.from(message));
    }

    public void addMessage(int index, Message message) {
        messages.add(index, message);
        conversation.add(index, ChatMessage.from(message));
    }

    public void setLastMessageAfter(Message referencedMessage, Message message) {
        int index = messages.indexOf(referencedMessage);
        messages.add(index + 1, message);
        conversation.add(index + 1, ChatMessage.from(message));
        // removes all messages after the new one
        messages.subList(index + 2, messages.size()).clear();
        conversation.subList(index + 2, conversation.size()).clear();
    }

    public List<ChatMessage> getMessages() {
        return conversation;
    }

    public JSONArray toJson() {
        return new JSONArray(this.getMessages().stream().map(ChatMessage::toJsonObject).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return String.join("\n\n", conversation.stream().map(it -> it.content).toList()) + "\n\n";
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
}
