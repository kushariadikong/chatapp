package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Calendar;

public class TextMessage extends Message {
    final String from, text;
    private final Long createdAt;

    @JsonCreator
    public TextMessage(
           @JsonProperty("from") 
           String from,
           @JsonProperty("text") 
           String text
    ) {
        super();
        this.from = from;
        this.text = text;
        this.createdAt = Calendar.getInstance().getTimeInMillis();
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }
    
    @JsonProperty("text")
    public String getText() {
        return text;
    }
    
    @JsonProperty("createdAt")
    public Long getCreatedAt() {
        return createdAt; 
    }

    @Override
    public String toString() {
        return String.format("TextMessage { \n"
                + "from: %s, \n"
                + "text: %s, \n"
                + "createdAt: %d \n"
                + "}",
                getFrom(), 
                getText(), 
                getCreatedAt());
    }
}
