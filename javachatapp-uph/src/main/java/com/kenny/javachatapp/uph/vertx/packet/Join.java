package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Join extends Message {
    private final String name;
    private final String room;

    @JsonCreator
    public Join(
        @JsonProperty("name") 
        String name,
        @JsonProperty("room") 
        String room
    ) {
        this.name = name;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return "Join {" +
                "name='" + name + "\'," +
                "room='" + room + '\'' +
                '}';
    }
}
