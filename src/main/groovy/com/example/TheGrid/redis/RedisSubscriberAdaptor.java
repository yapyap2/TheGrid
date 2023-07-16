package com.example.TheGrid.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@RequiredArgsConstructor
@Slf4j
public class RedisSubscriberAdaptor extends MessageListenerAdapter {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("adaptor log!");


        super.onMessage(message, pattern);
    }
}
