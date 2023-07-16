package com.example.TheGrid.dto;

import com.example.TheGrid.dto.GridMessage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class SendRequest {

    private String grid;

    private GridMessage message;

    @JsonCreator
    public SendRequest(@JsonProperty("grid")String grid, @JsonProperty("name")String name, @JsonProperty("body")String body, @JsonProperty("date")String date){

        this.grid = grid;
        GridMessage message = new GridMessage();
        message.setSender(name);
        message.setBody(body);
        message.setGrid(grid);

        if(date!=null){
            message.setCreateAt(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        }else message.setCreateAt(LocalDateTime.now());

        this.message = message;
    }
}
