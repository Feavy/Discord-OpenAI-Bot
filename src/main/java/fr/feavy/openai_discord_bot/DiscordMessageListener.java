package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.feavy.simpleopenai.OpenAIClient;
import fr.feavy.simpleopenai.CompletionEngine;

import static fr.feavy.simpleopenai.OpenAIClient.format;

public class DiscordMessageListener extends ListenerAdapter {
    private final OpenAIClient openai = new OpenAIClient(Settings.OPENAI_API_KEY);

    private final Map<String, DiscordConversation> cachedConversations = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) return;

        if(!Settings.ALLOWED_GUILDS.contains(event.getGuild().getId()))
            return;

        CompletionEngine engine = Settings.ENGINE;

        String contentRaw = format(event.getMessage().getContentRaw());

        for(Map.Entry<String, CompletionEngine> e : CompletionEngines.COMMON_ENGINES.entrySet()) {
            String name = e.getKey();
            CompletionEngine current = e.getValue();
            if(contentRaw.toLowerCase().endsWith(name.toLowerCase())) {
                contentRaw = contentRaw.substring(0, contentRaw.length() - name.length());
                engine = current;
                break;
            }
        }

        Message referencedMessage = event.getMessage().getReferencedMessage();

        DiscordConversation conv = cachedConversations.computeIfAbsent(author.getId(), (k -> new DiscordConversation()));

        if(referencedMessage == null) {
            if (contentRaw.startsWith("!ai") || contentRaw.endsWith("??")) {
                // New conversation
                conv = new DiscordConversation();
                conv.addMessage(event.getMessage());
            }else{
                return;
            }
        } else {
            if(conv.hasMessage(referencedMessage)) {
                // Reply to the user cached conversation
                conv.setLastMessageAfter(referencedMessage, event.getMessage());
            } else if(referencedMessage.getAuthor().getId().equals("959430211227750430") || referencedMessage.getAuthor().getId().equals("877228808846065665")) {
                // Reply to OpenAI
                conv = DiscordConversation.fromMessage(event.getMessage());
            } else {
                return;
            }
        }

        cachedConversations.put(author.getId(), conv);

        try {
            DiscordConversation finalConv = conv;
            CompletionEngine finalEngine = engine;
            openai.complete(conv, engine, Settings.MAX_TOKENS).thenAccept(completed -> {
                try {
                    if (completed == null)
                        return;

                    if(finalEngine.isChatBot) {
                        event.getMessage().reply(completed).queue(finalConv::addMessage, Throwable::printStackTrace);
                    } else {
                        String previousText = finalConv.toString();
                        completed = completed.replace(previousText, "");
                        event.getMessage().reply(completed).queue(finalConv::addMessage, Throwable::printStackTrace);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                event.getMessage().reply("An error occurred: " + e.getMessage()).queue();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
