package fr.feavy.openai_discord_bot.openai;

import org.json.JSONObject;

public class ChatMessage {
    public String role;
    public String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("role", this.role);
        obj.put("content", this.content);
        return obj;
    }
}
