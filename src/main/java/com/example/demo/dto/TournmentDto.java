package com.example.demo.dto;

import com.example.demo.entity.PlayerEntity;
import com.example.demo.entity.RoundEntity;

import java.util.ArrayList;
import java.util.List;

public record TournmentDto(
        Integer id,
        int roundsCount,
        List<RoundDto> rounds,
        List<PlayerDto>players
    ) {
    public void addRound(RoundDto round){
        rounds.add(round);
    }
    public TournmentDto clone(List<PlayerDto> players,List<RoundDto> rounds){
        return new TournmentDto(id,roundsCount,rounds,players);
    }
}
