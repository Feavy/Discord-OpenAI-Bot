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

    public CompletableFuture<String> complete(String input) {
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

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/engines/text-davinci-002/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "prompt": "%s",
                            "echo": true,
                            "temperature": 0.7,
                            "max_tokens": 256,
                            "top_p": 1,
                            "frequency_penalty": 0,
                            "presence_penalty": 0
                        }
                        """.formatted(input.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"")))).build();

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
            return format(choices.getJSONObject(0).getString("text"));
        }, executor);
    }

    public static String format(String input) {
        return input.replaceAll("<[^>]+>", "").replaceAll("\\t", "\t").replaceAll("\\n", "\n");
    }
}
