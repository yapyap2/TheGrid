package com.example.TheGrid.redis;

import com.example.TheGrid.dto.GridMessage;
import com.example.TheGrid.service.SocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final RedisTemplate redisTemplate;

    private final String name;

    private final SocketService socketService;
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String strBody = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        GridMessage gridMessage;
        try {
            gridMessage = objectMapper.readValue(strBody, GridMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        socketService.send(gridMessage, name);
    }
}
