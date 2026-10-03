package com.example.demo.mapper;

import com.example.demo.dto.PlayerDto;
import com.example.demo.dto.TournmentDto;
import com.example.demo.entity.PlayerEntity;
import com.example.demo.entity.TournmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TournmentMapper implements BaseMapper<TournmentEntity, TournmentDto>{
    @Autowired
    RoundMapper roundMapper;
    @Autowired
    PlayerMapper playerMapper;
    @Override
    public TournmentEntity mapToEntinity(TournmentDto dto) {
        if(dto==null)
            return null;
        return new TournmentEntity(dto.id(),dto.roundsCount(),
                dto.rounds()==null?null:dto.rounds().stream().map(e->roundMapper.mapToEntinity(e)).collect(Collectors.toList()),
                dto.players().stream().map(e->playerMapper.mapToEntinity(e)).collect(Collectors.toList()));
    }

    @Override
    public TournmentDto mapToDto(TournmentEntity entity) {
        if(entity==null)
            return null;
        return new TournmentDto(entity.getId(),entity.getRoundsCount(),
                entity.getRounds()==null?null:entity.getRounds().stream().map(e->roundMapper.mapToDto(e)).collect(Collectors.toList()),
                entity.getPlayers().stream().map(e->playerMapper.mapToDto(e)).collect(Collectors.toList()));
    }
}
