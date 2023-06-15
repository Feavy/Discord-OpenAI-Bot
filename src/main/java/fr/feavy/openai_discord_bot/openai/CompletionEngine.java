package fr.feavy.openai_discord_bot.openai;

public enum CompletionEngine {
    DAVINCI("gpt3", "text-davinci-003", false),
    GPT4("gpt4", "gpt-4", true);
    public final String name;
    public final String engineName;
    public final boolean isChatBot;

    CompletionEngine(String name, String engineName, boolean isChatBot) {
        this.name = name;
        this.engineName = engineName;
        this.isChatBot = isChatBot;
    }

    public static CompletionEngine fromEngineName(String name) {
        for (CompletionEngine value : values()) {
            if (value.engineName.equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CompletionEngine{" +
                "name='" + name + '\'' +
                ", engineName='" + engineName + '\'' +
                ", isChatBot=" + isChatBot +
                '}';
    }
}
