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
        COMMON_ENGINES.put("gpt3+", new CompletionEngine("gpt-3.5-turbo-16k", true, false));
        COMMON_ENGINES.put("gpt4+", new CompletionEngine("gpt-4-32k", true, false));
        COMMON_ENGINES.put("gpt4o", new CompletionEngine("gpt-4o", true, true));
        COMMON_ENGINES.put("gpt4o", new CompletionEngine("gpt-4o", true, true));
        COMMON_ENGINES.put("o3mini", new CompletionEngine("o3-mini", true, true));
        COMMON_ENGINES.put("o4mini", new CompletionEngine("o4-mini", true, true));
    }

    public static final CompletionEngine DEFAULT = COMMON_ENGINES.get("gpt4o");

    public static CompletionEngine get(String name) {
        CompletionEngine engine = COMMON_ENGINES.get(name);
        if(engine == null) {
            return new CompletionEngine(name, true, true)
        }
        return engine;
    }
}
