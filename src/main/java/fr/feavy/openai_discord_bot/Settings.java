package fr.feavy.openai_discord_bot;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Settings {
    public final static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    public final static List<String> ALLOWED_GUILDS = Arrays.asList(Optional.ofNullable(System.getenv("ALLOWED_GUILDS")).orElse("").split(","));
    public final static String ENGINE = System.getenv("ENGINE");
    // text-curie-001
    public static final int MAX_TOKENS = Integer.parseInt(System.getenv("MAX_TOKENS"));
}
