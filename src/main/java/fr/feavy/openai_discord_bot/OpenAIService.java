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
    private final OpenAIClient openai = new OpenAIClient(Settings.OPENAI_API_KEY);

    private final Map<String, Conversation> cachedConversations = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) return;

        if(!Settings.ALLOWED_GUILDS.contains(event.getGuild().getId()))
            return;

        CompletionEngine engine = Settings.ENGINE;

        String contentRaw = format(event.getMessage().getContentRaw());

        for(CompletionEngine e : CompletionEngine.values()) {
            if(contentRaw.toLowerCase().endsWith("["+e.name.toLowerCase()+"]")) {
                contentRaw = contentRaw.substring(0, contentRaw.length() - e.name.length() - 2);
                engine = e;
                break;
            }
        }

        Message referencedMessage = event.getMessage().getReferencedMessage();

        Conversation conv = cachedConversations.computeIfAbsent(author.getId(), (k -> new Conversation()));

        if(referencedMessage == null) {
            if (contentRaw.startsWith("!ai") || contentRaw.endsWith("??")) {
                // New conversation
                conv = new Conversation();
                conv.addMessage(event.getMessage());
            }else{
                return;
            }
        } else {
            if(conv.hasMessage(referencedMessage)) {
                // Reply to the user cached conversation
                conv.setLastMessageAfter(referencedMessage, event.getMessage());
            } else if(referencedMessage.getAuthor().getId().equals("959430211227750430")) {
                // Reply to OpenAI
                conv = Conversation.fromMessage(event.getMessage());
            } else {
                return;
            }
        }

        cachedConversations.put(author.getId(), conv);

        try {
            Conversation finalConv = conv;
            CompletionEngine finalEngine = engine;
            openai.complete(conv, engine).thenAccept(completed -> {
                try {
                    if (completed == null)
                        return;

                    if(finalEngine.isChatBot) {
                        event.getMessage().reply(completed).queue(finalConv::addMessage);
                    } else {
                        String previousText = finalConv.toString();
                        completed = completed.replace(previousText, "");
                        event.getMessage().reply(completed).queue(finalConv::addMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
