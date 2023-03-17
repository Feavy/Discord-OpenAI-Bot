package fr.feavy.openai_discord_bot;

public class Settings {
    public final static String ENGINE = System.getenv("ENGINE");
    // text-curie-001
    public static final int MAX_TOKENS = Integer.parseInt(System.getenv("MAX_TOKENS"));
}
