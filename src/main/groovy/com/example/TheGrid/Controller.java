package com.example.TheGrid;

import com.example.TheGrid.dto.GridMessage;
import com.example.TheGrid.dto.ListenerRequest;
import com.example.TheGrid.redis.GridTopic;
import com.example.TheGrid.redis.RedisSubscriber;
import com.example.TheGrid.dto.SendRequest;
import com.example.TheGrid.repository.MessageRepository;
import com.example.TheGrid.service.SocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final RedisMessageListenerContainer container;
    private final RedisTemplate redisTemplate;
    private final SocketService socketService;
    private final ObjectMapper objectMapper;

    private final MessageRepository messageRepository;
    private ConcurrentHashMap<String, RedisSubscriber> user = new ConcurrentHashMap<>();

    @PostMapping("/send")
    public String send(@RequestBody SendRequest sendRequest){
        log.info("send message from sender : {}   position : ", sendRequest.getMessage().getSender(), sendRequest.getGrid());
        redisTemplate.convertAndSend(sendRequest.getGrid(), sendRequest.getMessage());

        return "ok";
    }

    @PostMapping("/subscribe")
    public HashMap<String, Long> subscribe(@RequestBody ListenerRequest request){
        RedisSubscriber subscriber = user.get(request.getName());
        Set<ChannelTopic> grids = request.getGrids();

        if(subscriber == null){
            subscriber = new RedisSubscriber(redisTemplate, request.getName(), socketService, objectMapper);
            user.put(request.getName(), subscriber);

            container.addMessageListener(subscriber, grids);
        } else{
            container.removeMessageListener(subscriber);
            container.addMessageListener(subscriber, grids);
        }

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

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody String name){

        RedisSubscriber subscriber = user.get(name);
        container.removeMessageListener(subscriber);
        user.remove(name);
    }


    @GetMapping("/getMessage")
    public List<GridMessage> getMessage(@RequestParam String grid){
        return messageRepository.findAllByGrid(grid, LocalDateTime.now().minusHours(1L));
    }

}
