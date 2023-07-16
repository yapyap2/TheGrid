package com.example.TheGrid.redis;

import org.springframework.data.redis.listener.Topic;

public class GridTopic implements Topic {

    private String topic;

    public GridTopic(String topic) {
        this.topic = topic;
    }
    @Override
    public String getTopic() {
        return topic;
    }
}
