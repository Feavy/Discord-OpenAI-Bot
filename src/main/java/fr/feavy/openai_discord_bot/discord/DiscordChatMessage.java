package fr.feavy.openai_discord_bot.discord;

import fr.feavy.openai_discord_bot.openai.ChatMessage;
import net.dv8tion.jda.api.entities.Message;

public class DiscordChatMessage extends ChatMessage {
    public DiscordChatMessage(String role, String content) {
        super(role, content);
    }

    public static DiscordChatMessage from(Message message) {
        String role = message.getAuthor().isBot() ? "assistant" : "user";
        return new DiscordChatMessage(role, format(message.getContentRaw()));
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
