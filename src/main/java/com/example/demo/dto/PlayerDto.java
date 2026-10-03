package com.example.demo.dto;

import java.util.Map;

public record PlayerDto(
        Integer id,
        String name,
        int rating,
        int score,
        boolean byeReceived,
        Map<String,ResultDto> opponents
) {
    public PlayerDto updateBY(){
        return new PlayerDto(id,name,rating,score,true,opponents);
    }
}
