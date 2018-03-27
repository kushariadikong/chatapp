package com.kenny.javachatapp.uph.vertx.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.shareddata.Shareable;

import java.util.Objects;

public class User implements Shareable {
    private final String id, name, room;

    @JsonCreator
    public User(
            @JsonProperty("id")
            String id,
            @JsonProperty("name")
            String name,
            @JsonProperty("room")
            String room
    ) {
        this.id = id;
        this.name = name;
        this.room = room;
    }
    
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    @JsonProperty("room")
    public String getRoom() {
        return room;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User) o;
//        return Objects.equals(name, user.name);
//    }

//    @Override
//    public int hashCode() {
//
//        return Objects.hash(name);
//    }

    @Override
    public String toString() {
        return "User{" +
                "name: '" + name + '\'' +
                "room: '" + room + '\'' + '}';
    }
}
