package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fr.feavy.openai_discord_bot.OpenAIClient.format;

public class OpenAIService extends ListenerAdapter {
    private final Map<String, OpenAIClient> openAiByGuild = makeClients(System.getenv("OPENAI_TOKEN"));

    private final Map<String, UserAIConv> convs = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) {
            return;
        }
        OpenAIClient openai = openAiByGuild.get(event.getGuild().getId());
        if (openai == null) {
            return;
        }
        String contentRaw = format(event.getMessage().getContentRaw());

        Message lastMessage = event.getMessage().getReferencedMessage();

        UserAIConv conv = convs.computeIfAbsent(author.getId(), (k -> new UserAIConv()));

        if (lastMessage == null || conv.getLastBotMessage() == null || !lastMessage.getId().equals(conv.getLastBotMessage().getId())) {
            if (contentRaw.endsWith("??")) {
                contentRaw = contentRaw.substring(0, contentRaw.length() - 1);
            } else if (contentRaw.startsWith("!ai")) {
                contentRaw = contentRaw.substring(3)+"\n\n";
            } else {
                return;
            }
            conv.setText(contentRaw);
        } else {
            conv.addLine(contentRaw);
        }

        try {
            openai.complete(conv.text()).thenAccept(completed -> {
                try {
                    if (completed == null)
                        return;
                    String previousText = conv.text();
                    conv.setText(completed);
                    completed = completed.replace(previousText, "");
                    event.getMessage().reply(completed).queue(conv::setLastBotMessage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, OpenAIClient> makeClients(String tokenEnv) {
        Map<String, OpenAIClient> clients = new HashMap<>();
        String[] entries = tokenEnv.split("\n");
        for (String entry : entries) {
            String[] split = entry.split("=");
            String guildId = split[0];
            String openAiToken = split[1];
            clients.put(guildId, new OpenAIClient(openAiToken));
        }
        return clients;
    }
}
