package fr.feavy.openai_discord_bot;

import net.dv8tion.jda.api.entities.Message;

public class UserAIConv {
    private String text = "";
    private Message lastBotMessage;

    public UserAIConv() {
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addLine(String line) {
        this.text += "\n\n" + line + "\n\n";
    }

    public String text() {
        return text;
    }

    public void clear() {
        text = "";
    }

    public void setLastBotMessage(Message message) {
        this.lastBotMessage = message;
    }

    public Message getLastBotMessage() {
        return lastBotMessage;
    }

    @Override
    public String toString() {
        return text;
    }
}
