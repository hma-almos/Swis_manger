package com.example.demo.dto;

import com.example.demo.entity.MatchEntity;

import java.util.ArrayList;
import java.util.List;

public record RoundDto(
        Integer id,
        List<MatchDto> matches,
        int round
) {
    public void addMatch(MatchDto match){
        matches.add(match);
    }
}
