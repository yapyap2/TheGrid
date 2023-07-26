package com.example.TheGrid.service;

import com.example.TheGrid.dto.ListenerRequest;
import com.example.TheGrid.redis.RedisSubscriber;
import com.example.TheGrid.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriberContainer {

    private final RedisMessageListenerContainer container;
    private ConcurrentHashMap<String, RedisSubscriber> subscribers = new ConcurrentHashMap<>();

    private final RedisTemplate redisTemplate;

    private final SocketService socketService;

    private final ObjectMapper objectMapper;

    private final MessageRepository messageRepository;


    public RedisSubscriber get(String name){
        return subscribers.get(name);
    }

    public HashMap<String, Long> add(ListenerRequest request){

        RedisSubscriber subscriber = subscribers.get(request.getName());
        Set<ChannelTopic> grids = request.getGrids();

        if(subscriber == null){
            subscriber = new RedisSubscriber(redisTemplate, request.getName(), socketService, objectMapper);
            subscribers.put(request.getName(), subscriber);

            container.addMessageListener(subscriber, grids);
        } else{
            container.removeMessageListener(subscriber);
            container.addMessageListener(subscriber, grids);
        }

        log.info(" name: {} subscribed gird : {}", request.getName(), grids);

        List<String> strGrid = new ArrayList<>();
        grids.forEach(g ->{
            strGrid.add(g.getTopic());
        });

        List<Object[]> result = messageRepository.getAllMessageCount(strGrid, LocalDateTime.now().minusHours(1L));
        HashMap<String, Long> map = new HashMap<>();
        result.forEach(r ->{
            map.put((String) r[0], (Long) r[1]);
        });

        return map;
    }

    public void unsubscribe(String name){
        RedisSubscriber subscriber = subscribers.get(name);
        container.removeMessageListener(subscriber);
        subscribers.remove(name);
        log.info("name : {}  unsubscribed.", name);
    }
}
