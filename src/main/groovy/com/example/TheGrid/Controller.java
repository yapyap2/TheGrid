package com.example.TheGrid;

import com.example.TheGrid.dto.GridMessage;
import com.example.TheGrid.dto.ListenerRequest;
import com.example.TheGrid.redis.GridTopic;
import com.example.TheGrid.redis.RedisSubscriber;
import com.example.TheGrid.dto.SendRequest;
import com.example.TheGrid.repository.MessageRepository;
import com.example.TheGrid.service.SocketService;
import com.example.TheGrid.service.SubscriberContainer;
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
@CrossOrigin
public class Controller {
    private final RedisTemplate redisTemplate;

    private final MessageRepository messageRepository;

    private final SubscriberContainer subscriberContainer;
    @PostMapping("/send")
    public String send(@RequestBody SendRequest sendRequest){
        log.info("send message from sender : {}   position : ", sendRequest.getMessage().getSender(), sendRequest.getGrid());
        redisTemplate.convertAndSend(sendRequest.getGrid(), sendRequest.getMessage());

        return "ok";
    }

    @PostMapping("/subscribe")
    public HashMap<String, Long> subscribe(@RequestBody ListenerRequest request){
        return subscriberContainer.add(request);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody String name){
        subscriberContainer.unsubscribe(name);
    }


    @GetMapping("/getMessage")
    public List<GridMessage> getMessage(@RequestParam String grid){
        return messageRepository.findAllByGrid(grid, LocalDateTime.now().minusHours(1L));
    }

}
