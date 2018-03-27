package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenny.javachatapp.uph.vertx.model.User;

public class LoginNotification extends Message {
    private final User user;

    @JsonCreator
    public LoginNotification(
            @JsonProperty("user") 
            User user
    ) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginNotification{" +
                "user=" + user +
                '}';
    }
}
