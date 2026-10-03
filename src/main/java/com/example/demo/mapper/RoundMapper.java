package com.example.demo.mapper;

import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.RoundDto;
import com.example.demo.entity.PlayerEntity;
import com.example.demo.entity.RoundEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoundMapper implements BaseMapper<RoundEntity, RoundDto>{
    @Autowired
    MatchMapper mapper;

    @Override
    public RoundEntity mapToEntinity(RoundDto dto) {
        return new RoundEntity(dto.id(),
                dto.matches().stream().map(e->mapper.mapToEntinity(e)).collect(Collectors.toList()),
                dto.round());
    }

    @Override
    public RoundDto mapToDto(RoundEntity entity) {
        return new RoundDto(entity.getId(),entity.
                getMatches().stream().map(e->mapper.mapToDto(e)).collect(Collectors.toList()),
                entity.getRound());
    }
}
