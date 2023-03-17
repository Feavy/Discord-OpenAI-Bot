package fr.feavy.openai_discord_bot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class OpenAIClient {
    public final String token;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public OpenAIClient(String token) {
        this.token = token;
    }

    public CompletableFuture<String> complete(Conversation conversation) {
        String messages = new JSONArray(conversation.getMessages().stream().map(this::toJsonObject).collect(Collectors.toList())).toString();

        System.out.println("COMPLETE: "+ conversation);
        System.out.println(">>>");
        System.out.println("""
                        {
                            "model": "%s",
                            "messages": %s
                        }
                        """.formatted(Settings.ENGINE, messages));
        System.out.println(">>>");

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "model": "%s",
                            "messages": %s
                        }
                        """.formatted(Settings.ENGINE, messages))).build();

        return CompletableFuture.supplyAsync(() -> {
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String response = httpResponse.body();

            System.out.println("<<<");
            System.out.println(response);
            System.out.println("<<<");

            JSONArray choices = new JSONObject(response).getJSONArray("choices");
            String completed = format(choices.getJSONObject(0).getJSONObject("message").getString("content"));
//            System.out.println("COMPLETED: "+completed);
            return completed;
        }, executor);
    }

    private JSONObject toJsonObject(ChatMessage chatMessage) {
        JSONObject obj = new JSONObject();
        obj.put("role", chatMessage.role);
        obj.put("content", chatMessage.content);
        return obj;
    }

    public static String format(String input) {
        return input.replaceAll("<[^>]+>", "").replaceAll("\\t", "\t").replaceAll("\\n", "\n");
    }
}
