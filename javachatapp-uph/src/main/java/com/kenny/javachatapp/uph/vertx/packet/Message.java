/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use=JsonTypeInfo.Id.CLASS, 
        property="@class",
        include=JsonTypeInfo.As.PROPERTY
)
public class Message {
    
    @JsonCreator
    public Message() {}
    
    @Override
    public String toString() {
        return "Message {}";
    }
}