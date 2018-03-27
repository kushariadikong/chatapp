package com.kenny.javachatapp.uph.vertx.server;

import com.kenny.javachatapp.uph.vertx.model.User;
import com.kenny.javachatapp.uph.vertx.packet.ConversationMessage;
import com.kenny.javachatapp.uph.vertx.packet.ErrorMessage;
import com.kenny.javachatapp.uph.vertx.packet.Join;
import com.kenny.javachatapp.uph.vertx.packet.LoginNotification;
import com.kenny.javachatapp.uph.vertx.packet.Message;
import com.kenny.javachatapp.uph.vertx.packet.OnlineUsers;
import com.kenny.javachatapp.uph.vertx.packet.TextMessage;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import java.util.Optional;
import java.util.stream.Collectors;

class WSHandler {
    Vertx vertx;
    ServerWebSocket socket;
    User user;
    LocalMap<String, User> usersMap;
    
    public WSHandler(Vertx vertx, ServerWebSocket socket) {
        this.vertx = vertx;
        this.socket = socket;
        this.user = null;
        
        this.socket.handler(this::processMessage);
        this.socket.endHandler(hndlr -> {
            logout();
        });
    }
    
    void logout() {
        try {
            broadcastMessage(new TextMessage("Admin", String.format("%s has left.", user.getName())));
            usersMap.removeIfPresent(myselfAddress, user);
            broadcastOnlineUserListInRoom();
        } catch (NullPointerException e) {}
    }
    
    String myselfAddress;
    void join(Join j) {
        myselfAddress = "client-".concat(String.valueOf(socket.binaryHandlerID()));
        usersMap = vertx.sharedData().getLocalMap("users");
        User user = new User(myselfAddress, j.getName(), j.getRoom());
        
        Long nameExists = usersMap.values().stream()
            .map(u -> u.getName())
            .filter(name -> name.equals(j.getName().trim()))
            .count();

        if (nameExists.intValue() != 0) {
            renderMessage(new ErrorMessage("Admin", "Name already Exists!"));
            return;
        }
            
        
        if (usersMap.putIfAbsent(myselfAddress, user) == null) {
            this.user = user;
            
            renderMessage(new TextMessage("Admin", "Welcome to ChatApp!"));
            renderMessage(new LoginNotification(user));

            broadcastMessage(new TextMessage("Admin", String.format("%s joined the chat.", this.user.getName())));
            
            subscribeTo("broadcast/" + user.getRoom());
            broadcastOnlineUserListInRoom();
            
            subscribeTo("broadcast/" + user.getRoom() + "/" + user.getId());
        }
    }
    
    private void subscribeTo(String address) {
        vertx.eventBus().consumer(address).handler(vertxMessage -> {
            
            try {
            renderMessage(
                JsonObject.mapFrom(vertxMessage.body())
                    .mapTo(Message.class)
            );
            } catch(IllegalStateException e) {}
        });
    }
    
    void sendToClient(Message message) {
        socket.write(JsonObject.mapFrom(message).toBuffer());
    }
    
    void renderMessage(Message message) throws IllegalStateException {
        Message newMessage = message;
        
        if (message instanceof ConversationMessage) {
            
            if (((ConversationMessage) message).getToId() != null &&
                ((ConversationMessage) message).getFromId() != null &&
                ((ConversationMessage) message).getFromId().equals(user.getId())) {
                return;
            }
                
        } else if (message instanceof OnlineUsers) {

            newMessage = new OnlineUsers(
                ((OnlineUsers) message).getUsers()
                    .stream()
                    .filter(u -> {
                        return !u.getId().equals(user.getId());
                    })
                    .collect(Collectors.toList())
            );
        }
        
        socket.write(JsonObject.mapFrom(newMessage).toBuffer());
    }
    
    private void broadcastMessage(Message message) {
        if (message instanceof ConversationMessage) {
            if (((ConversationMessage) message).getToId() != null && 
                ((ConversationMessage) message).getToId() != "") {
                vertx.eventBus().publish("broadcast/" + user.getRoom() + "/" + ((ConversationMessage) message).getToId(), JsonObject.mapFrom(message));
                renderMessage(message);
                return;
            }
        }
        
        vertx.eventBus().publish("broadcast/" + user.getRoom(), JsonObject.mapFrom(message));
    }
    
    private void processMessage(Buffer buffer) {
        Message message = parseMessage(buffer).orElse(new Message());
        
        if (message instanceof OnlineUsers) {
            
            renderMessage(message);
            
        } else if (message instanceof ConversationMessage) {
            chat((ConversationMessage) message);
        } else if (message instanceof TextMessage) {
            message = new TextMessage(user.getName(), ((TextMessage) message).getText());
            broadcastMessage(message);
        } else if (message instanceof Join) {
            join((Join) message);
        } 
    }
    
    private Optional<Message> parseMessage(Buffer buffer) {
        JsonObject json = buffer.toJsonObject();
        Optional<Message> result;
        
        try {
            result = Optional.of(json.mapTo(Message.class));
        } catch (IllegalArgumentException e) {
            result = Optional.empty();
        }
        
        return result;
    }
    
    private void chat(ConversationMessage message) {
        ConversationMessage newMessage = new ConversationMessage(
            message.getText(),
            user.getName(),
            user.getId(),
            null
        );
        String to = message.getText().split(" ")[0];
        User toUser = null;
        
        try {
            toUser = usersMap.get(message.getToId());
        } catch(java.lang.NullPointerException e) {
            toUser = null;
        }
        
        if (toUser != null && toUser.getName().equals(to.replace("@", ""))) {
            renderMessage(newMessage);

            String toMessage = newMessage.getText().replace(to, "");
            newMessage = new ConversationMessage(toMessage, user.getName(), message.getFromId(), toUser.getId());
        }
        
        broadcastMessage(newMessage);
    }

    private void broadcastOnlineUserListInRoom() {
        
        broadcastMessage(new OnlineUsers(
                
            usersMap.values()
                .stream()
                .filter(u -> {
                    return u.getRoom().equals(user.getRoom());
                })
                .collect(Collectors.toList())
        ));
    }
}
