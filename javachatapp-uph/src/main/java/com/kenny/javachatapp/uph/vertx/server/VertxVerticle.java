/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kenny.javachatapp.uph.vertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import java.net.InetAddress;

class VertxVerticle extends AbstractVerticle {
    boolean isLocalhost = false;
    String ipaddress;

    @Override
    public void start() throws Exception {
        if (!isLocalhost) {
            ipaddress = InetAddress.getLocalHost().getHostAddress();
        } else {
            ipaddress = InetAddress.getByName("localhost").getHostAddress();
        }
        
        HttpServer httpServer = getVertx().createHttpServer();
        Router router = Router.router(getVertx());
        router.route("/*").handler(StaticHandler.create("webroot"));
        router.route("/css/*").handler(StaticHandler.create("webroot/css/"));
        router.route("/js/*").handler(StaticHandler.create("webroot/js/"));

        router.get("/").handler(context -> {
            context.response().sendFile("webroot/index.html");
        });
        
        router.get("/chat").handler(context -> {
            context.response().sendFile("webroot/chat.html");
        });

        httpServer.requestHandler(router::accept);
        httpServer.websocketHandler(socket -> new WSHandler(getVertx(), socket));
        httpServer.listen(3000, ipaddress, result -> {
            if (result.succeeded())
                System.out.println(String.format("Server start at %s:%s", ipaddress, httpServer.actualPort()));
            else
                System.out.println("Server failed to start.");
        });
    }
    
}