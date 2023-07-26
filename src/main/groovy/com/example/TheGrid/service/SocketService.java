package com.example.TheGrid.service;

import com.example.TheGrid.dto.GridMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SocketService{

    private final SessionContainer sessionContainer;

    private final ObjectMapper objectMapper;

    public void send(GridMessage message, String name){
        WebSocketSession session = sessionContainer.getSession(name);
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        TextMessage socketMessage = new TextMessage(json);
        try {
             session.sendMessage(socketMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
