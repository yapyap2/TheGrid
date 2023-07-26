package com.example.TheGrid.service;

import com.example.TheGrid.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageSocketHandler(), "/connect").setAllowedOriginPatterns("*");
    }


    private final SessionContainer sessionContainer;
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final MessageRepository messageRepository;
    private final SubscriberContainer subscriberContainer;
    @Bean
    public WebSocketHandler messageSocketHandler(){
        return new SocketHandler(sessionContainer, objectMapper, redisTemplate, messageRepository, subscriberContainer);
    }
}
