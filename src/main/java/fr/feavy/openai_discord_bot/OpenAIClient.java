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

public class OpenAIClient {
    public final String token;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public OpenAIClient(String token) {
        this.token = token;
    }


    public CompletableFuture<String> complete(Conversation conversation, CompletionEngine engine) {
        System.out.println("Completing with engine: " + engine.engineName + " (chatbot: " + engine.isChatBot + ")");
        return engine.isChatBot ? completeChatBot(conversation, engine.engineName) : completeClassic(conversation, engine.engineName);
    }

    public CompletableFuture<String> completeChatBot(Conversation conversation, String engineName) {
        String messages = conversation.toJson().toString();

//        System.out.println("COMPLETE: "+ conversation);
//        System.out.println(">>>");
//        System.out.println("""
//                        {
//                            "model": "%s",
//                            "messages": %s,
//                            "max_tokens": %d
//                        }
//                        """.formatted(Settings.ENGINE, messages, Settings.MAX_TOKENS));
//        System.out.println(">>>");

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "model": "%s",
                            "messages": %s,
                            "max_tokens": %d
                        }
                        """.formatted(engineName, messages, Settings.MAX_TOKENS))).build();

        return CompletableFuture.supplyAsync(() -> {
            HttpResponse<String> httpResponse;
            try {
                httpResponse = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String response = httpResponse.body();

//            System.out.println("<<<");
//            System.out.println(response);
//            System.out.println("<<<");

            JSONArray choices = new JSONObject(response).getJSONArray("choices");
            String completed = format(choices.getJSONObject(0).getJSONObject("message").getString("content"));
//            System.out.println("COMPLETED: "+completed);
            return completed;
        }, executor);
    }

    public CompletableFuture<String> completeClassic(Conversation conversation, String engineName) {
        String input = conversation.toString();
//        System.out.println("COMPLETE: "+input);
//        System.out.println(">>>");
//        System.out.println("""
//                        {
//                            "prompt": "%s",
//                            "echo": true,
//                            "temperature": 0.7,
//                            "max_tokens": 256,
//                            "top_p": 1,
//                            "frequency_penalty": 0,
//                            "presence_penalty": 0
//                        }
//                        """.formatted(input.replaceAll("\n", "\\\\n")));
//        System.out.println(">>>");

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/engines/"+engineName+"/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "prompt": "%s",
                            "echo": true,
                            "temperature": 0.7,
                            "max_tokens": %d,
                            "top_p": 1,
                            "frequency_penalty": 0,
                            "presence_penalty": 0
                        }
                        """.formatted(input.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\""), Settings.MAX_TOKENS))).build();

        return CompletableFuture.supplyAsync(() -> {
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String response = httpResponse.body();

//            System.out.println("<<<");
//            System.out.println(response);
//            System.out.println("<<<");

            JSONArray choices = new JSONObject(response).getJSONArray("choices");
            String completed = format(choices.getJSONObject(0).getString("text"));
//            System.out.println("COMPLETED: "+completed);
            return completed;
        }, executor);
    }

    public static String format(String input) {
        return input.replaceAll("\\t", "\t").replaceAll("\\n", "\n");
    }
}
