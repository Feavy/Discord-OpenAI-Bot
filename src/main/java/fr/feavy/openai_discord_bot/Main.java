package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA api;
    public static String token;

    public static void main(String[] args) throws LoginException, InterruptedException {
        token = System.getenv("BOT_TOKEN");
        api = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES).build();
        api.awaitReady();

        api.addEventListener(new OpenAIService());
    }
}
