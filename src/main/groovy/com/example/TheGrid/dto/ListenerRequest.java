package com.example.TheGrid.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ListenerRequest {

    private String name;
    private Set<ChannelTopic> grids = new HashSet<>();

    @JsonCreator
    public ListenerRequest(@JsonProperty("name") String name, @JsonProperty("grids") List<String> grids){
        this.name = name;

        grids.forEach(g -> {
            ChannelTopic topic = new ChannelTopic(g);
            this.grids.add(topic);
        });

    }
}
