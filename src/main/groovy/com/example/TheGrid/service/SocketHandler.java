package com.example.TheGrid.service;

import com.example.TheGrid.dto.GridMessage;
import com.example.TheGrid.dto.SendRequest;
import com.example.TheGrid.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
@Slf4j
public class SocketHandler extends TextWebSocketHandler {

    private final SessionContainer sessionContainer;
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final MessageRepository messageRepository;

    private final SubscriberContainer subscriberContainer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String name = session.getUri().getQuery().split("=")[1];
        sessionContainer.addSession(session, name);
        log.info("new Socket connection established   name : {}", name);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String strBody = message.getPayload();
        SendRequest sendRequest = objectMapper.readValue(strBody, SendRequest.class);
        GridMessage gridMessage = sendRequest.getMessage();

        messageRepository.save(gridMessage);

        redisTemplate.convertAndSend(sendRequest.getGrid(), gridMessage);
        log.info("Socket Received Message.   sender : {}", gridMessage.getSender());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String name = session.getUri().getQuery().split("=")[1];
        sessionContainer.deleteSession(name);
        subscriberContainer.unsubscribe(name);
        log.info("Socket Disconnected UserName: {}", name);
        super.afterConnectionClosed(session, status);
    }
}
