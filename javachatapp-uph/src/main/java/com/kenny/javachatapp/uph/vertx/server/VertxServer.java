package com.kenny.javachatapp.uph.vertx.server;

import io.vertx.core.Vertx;

public class VertxServer {
    public static void main(String []args) throws java.io.IOException {
        Vertx vertx = Vertx.vertx();

        // deploy server
        VertxVerticle verticle = new VertxVerticle();
        vertx.deployVerticle(verticle);
        
        System.out.println("Press ENTER to stop the server.");
        System.in.read();
        vertx.close();
    }
}
