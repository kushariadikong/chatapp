package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversationMessage extends TextMessage {
    private final String from, fromId;
    private final String toId;

    @JsonCreator
    public ConversationMessage(@JsonProperty("text") String text,
                               @JsonProperty("from") String from,
                               @JsonProperty("fromId") String fromId,
                               @JsonProperty("toId") String toId) {
        super(from, text);
        this.from = from;
        this.fromId = fromId;
        this.toId = toId;
    }

    public String getFrom() {
        return from;
    }
    
    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    @Override
    public String toString() {
        return "ConversationMessage{" +
                "from='" + from + '\'' +
                ", toId='" + toId + '\'' +
                '}';
    }
}
