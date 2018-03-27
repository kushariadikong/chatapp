/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kenny.javachatapp.uph.vertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author kennywang
 */
public class ErrorMessage extends TextMessage {

    @JsonCreator
    public ErrorMessage(
           @JsonProperty("from") 
           String from,
           @JsonProperty("text") 
           String text
    ) {
        super(from, text);
    }
    
}
