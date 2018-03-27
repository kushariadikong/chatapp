package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenny.javachatapp.uph.vertx.model.User;

import java.util.Collection;

public class OnlineUsers extends Message {
    private final Collection<User> users;

    @JsonCreator
    public OnlineUsers(
            @JsonProperty("users") 
            Collection<User> users
    ) {
        this.users = users;
    }

    public Collection<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "OnlineUsers{" +
                "users=" + users +
                '}';
    }
}
