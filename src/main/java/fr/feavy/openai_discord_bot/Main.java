package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static JDA api;
    public static String token;

    public static void main(String[] args) throws InterruptedException {
        token = System.getenv("BOT_TOKEN");
        api = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).build();
        api.awaitReady();

        api.addEventListener(new DiscordMessageListener());

        System.out.println("OpenAI bot ready. Engine: "+Settings.ENGINE);
    }
}
