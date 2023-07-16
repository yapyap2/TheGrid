package com.example.TheGrid.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

@Component
public class SessionContainer {


    private HashMap<String, WebSocketSession> sessions = new HashMap<>();


    public void addSession(WebSocketSession session, String name){
        sessions.put(name, session);
    }

    public void deleteSession(String name){
        sessions.remove(name);
    }

    public WebSocketSession getSession(String name){
        return sessions.get(name);
    }

}
