package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;

import fr.feavy.simpleopenai.ChatMessage;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class DiscordChatMessage extends ChatMessage {
    public DiscordChatMessage(String role, String content, List<String> imageUrls) {
        super(role, content, imageUrls);
    }

    public static DiscordChatMessage from(Message message) {
        String role = message.getAuthor().isBot() ? "assistant" : "user";

        return new DiscordChatMessage(role, format(message.getContentRaw()), getImageUrls(message));
    }

    private static String format(String contentRaw) {
        if (contentRaw.endsWith("??")) {
            contentRaw = contentRaw.substring(0, contentRaw.length() - 1);
        } else if (contentRaw.startsWith("!ai")) {
            contentRaw = contentRaw.substring(3);
        }
        return contentRaw;
    }

    private static List<String> getImageUrls(Message message) {
        List<String> imageUrls = new ArrayList<>();
        for(Message.Attachment attachment : message.getAttachments()) {
            if(attachment.isImage()) {
                imageUrls.add(attachment.getUrl());
            }
        }
        for(MessageEmbed embed : message.getEmbeds()) {
            if(embed.getImage() != null) {
                imageUrls.add(embed.getImage().getUrl());
            }
            if(embed.getThumbnail() != null) {
                imageUrls.add(embed.getThumbnail().getUrl());
            }
        }
        return imageUrls;
    }
}
