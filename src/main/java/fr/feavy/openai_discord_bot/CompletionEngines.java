package fr.feavy.openai_discord_bot;

import fr.feavy.simpleopenai.CompletionEngine;

import java.util.HashMap;
import java.util.Map;

public class CompletionEngines {
    public static final Map<String, CompletionEngine> COMMON_ENGINES = new HashMap<>();

    static {
        COMMON_ENGINES.put("gpt3", CompletionEngine.DAVINCI);
        COMMON_ENGINES.put("gpt4", CompletionEngine.GPT4);
        COMMON_ENGINES.put("vision", CompletionEngine.GPT4_VISION);
        COMMON_ENGINES.put("gpt3+", new CompletionEngine("gpt-3.5-turbo-16k", true));
        COMMON_ENGINES.put("gpt4+", new CompletionEngine("gpt-4-32k", true));
    }

    public static CompletionEngine get(String name) {
        return COMMON_ENGINES.get(name);
    }
}
