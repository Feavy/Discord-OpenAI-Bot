package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;

public class ChatMessage {
    public String role;
    public String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static ChatMessage from(Message message) {
        String role = message.getAuthor().isBot() ? "assistant" : "user";
        return new ChatMessage(role, format(message.getContentRaw()));
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
