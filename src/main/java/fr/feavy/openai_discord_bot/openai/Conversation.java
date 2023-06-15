package fr.feavy.openai_discord_bot.openai;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Conversation {
    private final List<ChatMessage> conversation = new ArrayList<>();

    public Conversation() {
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
}
